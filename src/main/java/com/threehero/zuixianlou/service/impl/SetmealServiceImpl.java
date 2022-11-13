package com.threehero.zuixianlou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threehero.zuixianlou.common.CustomException;
import com.threehero.zuixianlou.dto.SetmealDto;
import com.threehero.zuixianlou.mapper.SetmealMapper;
import com.threehero.zuixianlou.pojo.Setmeal;
import com.threehero.zuixianlou.pojo.SetmealDish;
import com.threehero.zuixianlou.service.SetmealDishService;
import com.threehero.zuixianlou.service.SetmealService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

  @Autowired
  private SetmealDishService setmealDishService;

  @Override
  @Transactional
  public void saveWithDish(SetmealDto setmealDto) {
    this.save(setmealDto);

    List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
    setmealDishes.stream().map(item -> {
      item.setSetmealId(setmealDto.getId());
      return item;
    }).collect(Collectors.toList());

    setmealDishService.saveBatch(setmealDishes);
  }

  @Override
  public SetmealDto getByIdWithDish(Long id) {
    Setmeal setmeal = this.getById(id);
    SetmealDto setmealDto = new SetmealDto();

    BeanUtils.copyProperties(setmeal, setmealDto);

    LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();

    queryWrapper.eq(SetmealDish::getSetmealId, setmeal.getId());
    List<SetmealDish> dishes = setmealDishService.list(queryWrapper);
    setmealDto.setSetmealDishes(dishes);

    return setmealDto;

  }

  @Override
  public void updateWithDish(SetmealDto setmealDto) {
    this.updateById(setmealDto);

    LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
    setmealDishService.remove(queryWrapper);
    List<SetmealDish> dishes = setmealDto.getSetmealDishes();
    dishes.stream().map(item -> {
      item.setSetmealId(setmealDto.getId());
      return item;
    }).collect(Collectors.toList());

    setmealDishService.saveBatch(dishes);
  }

  @Override
  public void removeWithDish(List<Long> ids) {
    // 查询套餐状态
    LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.in(Setmeal::getId, ids);
    queryWrapper.eq(Setmeal::getStatus, 1);

    int count = this.count(queryWrapper);

    if (count > 0) {
      throw new CustomException("套餐正在售卖中, 不能删除");
    }

    // 删除套餐数据
    this.removeByIds(ids);

    // 删除关系表中数据
    LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
    lambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
    setmealDishService.remove(lambdaQueryWrapper);

  }


}
