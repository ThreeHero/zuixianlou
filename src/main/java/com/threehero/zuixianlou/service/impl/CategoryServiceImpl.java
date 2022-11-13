package com.threehero.zuixianlou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threehero.zuixianlou.common.CustomException;
import com.threehero.zuixianlou.mapper.CategoryMapper;
import com.threehero.zuixianlou.pojo.Category;
import com.threehero.zuixianlou.pojo.Dish;
import com.threehero.zuixianlou.pojo.Setmeal;
import com.threehero.zuixianlou.service.CategoryService;
import com.threehero.zuixianlou.service.DishService;
import com.threehero.zuixianlou.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

  @Autowired
  private DishService dishService;

  @Autowired
  private SetmealService setmealService;

  @Override
  public void remove(Long id) {
    LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
    dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
    int count = dishService.count(dishLambdaQueryWrapper);

    if (count > 0) {
      throw new CustomException("当前分类下有菜品，不能删除");
    }

    LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
    setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
    int count1 = setmealService.count(setmealLambdaQueryWrapper);

    if (count1 > 0) {
      throw new CustomException("当前分类下有套餐，不能删除");
    }

    super.removeById(id);

  }
}
