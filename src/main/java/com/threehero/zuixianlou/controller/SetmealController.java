package com.threehero.zuixianlou.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.threehero.zuixianlou.common.R;
import com.threehero.zuixianlou.dto.SetmealDto;
import com.threehero.zuixianlou.pojo.Category;
import com.threehero.zuixianlou.pojo.Setmeal;
import com.threehero.zuixianlou.service.CategoryService;
import com.threehero.zuixianlou.service.SetmealDishService;
import com.threehero.zuixianlou.service.SetmealService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

  @Autowired
  private SetmealService setmealService;

  @Autowired
  private SetmealDishService setmealDishService;

  @Autowired
  private CategoryService categoryService;

  /**
   * 新增套餐
   * @param setmealDto
   * @return
   */
  @PostMapping
  @CacheEvict(value = "setmealCache", allEntries = true)
  public R<String> save(@RequestBody SetmealDto setmealDto) {

    setmealService.saveWithDish(setmealDto);
    return R.success("新增套餐成功");
  }

  /**
   * 套餐分页查询
   * @param page
   * @param pageSize
   * @param name
   * @return
   */
  @GetMapping("/page")
  public R<Page> page(int page, int pageSize, String name) {
    Page<Setmeal> pageInfo = new Page<>(page, pageSize);
    Page<SetmealDto> setmealDtoPage = new Page<>();

    LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.like(name != null, Setmeal::getName, name);
    queryWrapper.orderByDesc(Setmeal::getUpdateTime);
    setmealService.page(pageInfo, queryWrapper);

    BeanUtils.copyProperties(pageInfo, setmealDtoPage, "records");
    List<Setmeal> records = pageInfo.getRecords();
    List<SetmealDto> list = records.stream().map(item -> {
      SetmealDto setmealDto = new SetmealDto();
      BeanUtils.copyProperties(item, setmealDto);

      Long categoryId = item.getCategoryId();

      Category category = categoryService.getById(categoryId);
      if (category != null) {
        String categoryName = category.getName();
        setmealDto.setCategoryName(categoryName);
      }
      return setmealDto;
    }).collect(Collectors.toList());

    setmealDtoPage.setRecords(list);

    return R.success(setmealDtoPage);


  }

  /**
   * 套餐根据id查询
   * @param id
   * @return
   */
  @GetMapping("/{id}")
  public R<SetmealDto> get(@PathVariable Long id) {
    SetmealDto setmealDto = setmealService.getByIdWithDish(id);
    return R.success(setmealDto);
  }

  /**
   * 修改套餐
   * @param setmealDto
   * @return
   */
  @PutMapping
  @CacheEvict(value = "setmealCache", allEntries = true)
  public R<String> update(@RequestBody SetmealDto setmealDto) {
    setmealService.updateWithDish(setmealDto);

    return R.success("修改套餐成功");
  }

  /**
   * 删除套餐
   * @param id
   * @return
   */
  @DeleteMapping
  @CacheEvict(value = "setmealCache", allEntries = true)
  public R<String> delete(@RequestParam List<Long> ids) {
    log.info("id: {}", ids);
    setmealService.removeWithDish(ids);
    return R.success("套餐删除成功");
  }

  @PostMapping("/status/{status}")
  @CacheEvict(value = "setmealCache", allEntries = true)
  public R<String> toggle(@PathVariable Integer status, @RequestParam Long[] ids) {
    List<Setmeal> setmeals = new ArrayList<>();
    Arrays.stream(ids).map(setmealId -> setmealService.getById(setmealId)).forEach(setmeal -> {
      setmeal.setStatus(status);
      setmeals.add(setmeal);
    });
    setmealService.updateBatchById(setmeals);

    return R.success("套餐售卖状态切换成功");
  }

  /**
   * 根据条件查询套餐数据
   * @param setmeal
   * @return
   */
  @GetMapping("/list")
  @Cacheable(value = "setmealCache", key = "#setmeal.categoryId + '_' + #setmeal.status")
  public R<List<Setmeal>> list(Setmeal setmeal){
    LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
    queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
    queryWrapper.orderByDesc(Setmeal::getUpdateTime);

    List<Setmeal> list = setmealService.list(queryWrapper);

    return R.success(list);
  }

}
