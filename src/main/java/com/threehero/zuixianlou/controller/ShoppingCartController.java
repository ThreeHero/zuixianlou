package com.threehero.zuixianlou.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.threehero.zuixianlou.common.BaseContext;
import com.threehero.zuixianlou.common.R;
import com.threehero.zuixianlou.pojo.ShoppingCart;
import com.threehero.zuixianlou.service.ShoppingCartService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {


  @Autowired
  private ShoppingCartService shoppingCartService;

  @PostMapping("/add")
  public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
    Long currentId = BaseContext.getCurrentId();
    shoppingCart.setUserId(currentId);

    Long dishId = shoppingCart.getDishId();
    LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(ShoppingCart::getUserId, currentId);
    if (dishId != null) {
      // 菜品
      queryWrapper.eq(ShoppingCart::getDishId, dishId);
    } else {
      queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
    }
    ShoppingCart one = shoppingCartService.getOne(queryWrapper);
    if (one != null) {
      Integer number = one.getNumber();
      one.setNumber(number + 1);
      shoppingCartService.updateById(one);
    } else {
      shoppingCart.setNumber(1);
      shoppingCartService.save(shoppingCart);
      one = shoppingCart;
    }

    return R.success(one);
  }

  @GetMapping("/list")
  public R<List<ShoppingCart>> list(){

    LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
    queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

    List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

    return R.success(list);
  }

  @DeleteMapping("/clean")
  public R<String> remove() {
    // 条件构造器
    LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
    // 查询当前用户id
    queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

    shoppingCartService.remove(queryWrapper);

    return R.success("清空购物车成功");
  }

  @PostMapping("/sub")
  public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {
    // 获取用户id
    Long currentId = BaseContext.getCurrentId();
    LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(ShoppingCart::getUserId, currentId);
    Long dishId = shoppingCart.getDishId();
    if (dishId != null) {
      // 菜品
      queryWrapper.eq(ShoppingCart::getDishId, dishId);
    } else {
      queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
    }
    ShoppingCart one = shoppingCartService.getOne(queryWrapper);
    Integer number = one.getNumber();
    if (number != 1) {
      one.setNumber(number - 1);
      shoppingCartService.updateById(one);
    } else {
      shoppingCartService.removeById(one);
    }
    return R.success(one);

  }




}


