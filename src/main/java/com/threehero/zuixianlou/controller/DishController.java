package com.threehero.zuixianlou.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.threehero.zuixianlou.common.R;
import com.threehero.zuixianlou.dto.DishDto;
import com.threehero.zuixianlou.pojo.Category;
import com.threehero.zuixianlou.pojo.Dish;
import com.threehero.zuixianlou.pojo.DishFlavor;
import com.threehero.zuixianlou.service.CategoryService;
import com.threehero.zuixianlou.service.DishFlavorService;
import com.threehero.zuixianlou.service.DishService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

  @Autowired
  private DishService dishService;

  @Autowired
  private DishFlavorService dishFlavorService;

  @Autowired
  private CategoryService categoryService;

  /**
   * 新增菜品
   * @param dishDto
   * @return
   */
  @PostMapping
  public R<String> save(@RequestBody DishDto dishDto) {
    dishService.saveWithFlavor(dishDto);
    return R.success("新增菜品成功");
  }

  /**
   * 分页
   * @param page
   * @param pageSize
   * @param name
   * @return
   */
  @GetMapping("/page")
  public R<Page> page(int page, int pageSize, String name) {
    Page<Dish> pageInfo = new Page<>(page, pageSize);
    Page<DishDto> dishDtoPage = new Page<>();

    LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.like(name != null, Dish::getName, name);
    queryWrapper.orderByDesc(Dish::getUpdateTime);
    dishService.page(pageInfo,queryWrapper);

    BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
    List<Dish> records = pageInfo.getRecords();
    List<DishDto> list = records.stream().map(item -> {
      DishDto dishDto = new DishDto();

      BeanUtils.copyProperties(item,dishDto);

      Long categoryId = item.getCategoryId();//分类id
      //根据id查询分类对象
      Category category = categoryService.getById(categoryId);

      if (category != null) {
        String categoryName = category.getName();
        dishDto.setCategoryName(categoryName);
      }
      return dishDto;
    }).collect(Collectors.toList());

    dishDtoPage.setRecords(list);

    return R.success(dishDtoPage);
  }

  /**
   * 根据id查询菜品
   * @param id
   * @return
   */
  @GetMapping("/{id}")
  public R<DishDto> get(@PathVariable Long id) {
    DishDto dishDto = dishService.getByIdWithFlavor(id);
    return R.success(dishDto);
  }

  /**
   * 修改菜品
   * @param dishDto
   * @return
   */
  @PutMapping
  public R<String> update(@RequestBody DishDto dishDto) {
    dishService.updateWithFlavor(dishDto);

    return R.success("修改菜品成功");
  }

  /**
   * 删除菜品
   * @param id
   * @return
   */
  @DeleteMapping
  public R<String> delete(@RequestParam List<Long> ids) {
    dishService.removeWithFlavor(ids);
    return R.success("删除商品成功");
  }

  /**
   * 切换商品售卖状态
   * @param status
   * @param id
   * @return
   */
  @PostMapping("/status/{status}")
  public R<String> toggle(@PathVariable Integer status, @RequestParam Long[] ids) {
    List<Dish> dishes = new ArrayList<>();

    Arrays.stream(ids).map(dishId -> dishService.getById(dishId)).forEach(dish -> {
      dish.setStatus(status);
      dishes.add(dish);
    });
    dishService.updateBatchById(dishes);

    return R.success("菜品售卖状态切换成功");
  }

  @GetMapping("/list")
  public R<List<DishDto>> list(Dish dish) {
    LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
    queryWrapper.like(dish.getName() != null, Dish::getName, dish.getName());
    queryWrapper.eq(Dish::getStatus, 1);
    queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
    List<Dish> list = dishService.list(queryWrapper);


    List<DishDto> dishDtoList = list.stream().map(item -> {
      DishDto dishDto = new DishDto();

      BeanUtils.copyProperties(item,dishDto);

      Long categoryId = item.getCategoryId();//分类id
      //根据id查询分类对象
      Category category = categoryService.getById(categoryId);

      if (category != null) {
        String categoryName = category.getName();
        dishDto.setCategoryName(categoryName);
      }
      Long dishId = item.getId();
      LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
      lambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
      List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
      dishDto.setFlavors(dishFlavorList);
      return dishDto;
    }).collect(Collectors.toList());

    return R.success(dishDtoList);

  }

 }
