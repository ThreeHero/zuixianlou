package com.threehero.zuixianlou.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threehero.zuixianlou.pojo.Orders;

public interface OrderService extends IService<Orders> {

  void submit(Orders orders);
}
