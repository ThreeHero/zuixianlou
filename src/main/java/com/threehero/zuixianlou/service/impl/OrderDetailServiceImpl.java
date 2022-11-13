package com.threehero.zuixianlou.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threehero.zuixianlou.mapper.OrderDetailMapper;
import com.threehero.zuixianlou.pojo.OrderDetail;
import com.threehero.zuixianlou.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}
