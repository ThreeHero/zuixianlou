package com.threehero.zuixianlou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threehero.zuixianlou.common.CustomException;
import com.threehero.zuixianlou.dto.DishDto;
import com.threehero.zuixianlou.mapper.DishMapper;
import com.threehero.zuixianlou.pojo.Dish;
import com.threehero.zuixianlou.pojo.DishFlavor;
import com.threehero.zuixianlou.service.DishFlavorService;
import com.threehero.zuixianlou.service.DishService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

  @Autowired
  private DishFlavorService dishFlavorService;

  @Override
  @Transactional
  public void saveWithFlavor(DishDto dishDto) {
    this.save(dishDto);
    Long dishId = dishDto.getId();
    List<DishFlavor> flavors = dishDto.getFlavors();
    flavors.stream().map(item -> {
      item.setDishId(dishId);
      return item;
    }).collect(Collectors.toList());
    dishFlavorService.saveBatch(flavors);
  }

  @Override
  public DishDto getByIdWithFlavor(Long id) {
    Dish dish = this.getById(id);
    DishDto dishDto = new DishDto();
    BeanUtils.copyProperties(dish, dishDto);

    LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();

    queryWrapper.eq(DishFlavor::getDishId, dish.getId());
    List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
    dishDto.setFlavors(flavors);

    return dishDto;
  }

  @Override
  public void updateWithFlavor(DishDto dishDto) {
    this.updateById(dishDto);

    LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
    dishFlavorService.remove(queryWrapper);
    List<DishFlavor> flavors = dishDto.getFlavors();
    flavors.stream().map(item -> {
      item.setDishId(dishDto.getId());
      return item;
    }).collect(Collectors.toList());
    dishFlavorService.saveBatch(flavors);
  }

  @Override
  public void removeWithFlavor(List<Long> ids) {
    LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.in(Dish::getId, ids);
    queryWrapper.eq(Dish::getStatus, 1);

    int count = this.count(queryWrapper);

    if (count > 0) {
      throw new CustomException("商品正在售卖中不可以删除, 请先停售");
    }

    this.removeByIds(ids);

    LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
    lambdaQueryWrapper.in(DishFlavor::getDishId, ids);
    dishFlavorService.remove(lambdaQueryWrapper);
  }


}
