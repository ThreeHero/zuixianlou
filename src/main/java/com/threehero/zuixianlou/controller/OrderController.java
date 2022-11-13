package com.threehero.zuixianlou.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.threehero.zuixianlou.common.BaseContext;
import com.threehero.zuixianlou.common.CustomException;
import com.threehero.zuixianlou.common.R;
import com.threehero.zuixianlou.pojo.Orders;
import com.threehero.zuixianlou.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {

  @Autowired
  private OrderService orderService;

  @PostMapping("/submit")
  public R<String> submit(@RequestBody Orders orders) {
    orderService.submit(orders);
    return R.success("下单成功");
  }

  @GetMapping("/page")
  public R<Page> page(int page, int pageSize, Long number, String beginTime, String endTime) {

    // 创建分页插件
    Page<Orders> ordersPage = new Page<>(page, pageSize);
    LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.like(number != null, Orders::getNumber, number)
        .gt(StringUtils.isNotEmpty(beginTime),Orders::getOrderTime,beginTime)
        .lt(StringUtils.isNotEmpty(endTime),Orders::getOrderTime,endTime);

    orderService.page(ordersPage, queryWrapper);
    return R.success(ordersPage);

  }

  @PutMapping
  public R<Orders> update(@RequestBody Orders orders) {
    // 修改订单状态
    Integer status = orders.getStatus();
    log.info("status: {}", status);
    // 判断是否传入订单状态
    if (status != null) {
      orders.setStatus(status);
    } else {
      throw new CustomException("订单状态异常");
    }

    orderService.updateById(orders);
    return R.success(orders);

  }

  @GetMapping("/userPage")
  public R<Page> userPage(int page, int pageSize) {
       //分页构造器对象
       Page<Orders> pageInfo = new Page<>(page, pageSize);
       //构造条件查询对象
       LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();

       //添加排序条件，根据更新时间降序排列
       queryWrapper.orderByDesc(Orders::getOrderTime);
       orderService.page(pageInfo, queryWrapper);

       return R.success(pageInfo);
  }

  @PostMapping("/again")
  public R<String> again(@RequestBody Orders orders) {
    // 查询用户id
    Long currentId = BaseContext.getCurrentId();
    log.info("userId: {}", currentId);
    LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(Orders::getId, orders.getId()).eq(Orders::getUserId, currentId);
    Orders one = orderService.getOne(queryWrapper);
    one.setStatus(2);
    orderService.updateById(one);
    return R.success("再来一单");
  }


}
