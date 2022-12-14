package com.threehero.zuixianlou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threehero.zuixianlou.common.BaseContext;
import com.threehero.zuixianlou.common.CustomException;
import com.threehero.zuixianlou.mapper.OrderMapper;
import com.threehero.zuixianlou.pojo.AddressBook;
import com.threehero.zuixianlou.pojo.OrderDetail;
import com.threehero.zuixianlou.pojo.Orders;
import com.threehero.zuixianlou.pojo.ShoppingCart;
import com.threehero.zuixianlou.pojo.User;
import com.threehero.zuixianlou.service.AddressBookService;
import com.threehero.zuixianlou.service.OrderDetailService;
import com.threehero.zuixianlou.service.OrderService;
import com.threehero.zuixianlou.service.ShoppingCartService;
import com.threehero.zuixianlou.service.UserService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

  @Autowired
  private ShoppingCartService shoppingCartService;

  @Autowired
  private UserService userService;

  @Autowired
  private AddressBookService addressBookService;

  @Autowired
  private OrderDetailService orderDetailService;

  @Override
  @Transactional
  public void submit(Orders orders) {
    Long currentId = BaseContext.getCurrentId();
    LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(ShoppingCart::getUserId, currentId);
    List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);

    if (shoppingCartList == null || shoppingCartList.size() == 0) {
      throw new CustomException("???????????????, ????????????");
    }

    User user = userService.getById(currentId);
    Long addressBookId = orders.getAddressBookId();
    AddressBook addressBook = addressBookService.getById(addressBookId);

    if (addressBook == null) {
      throw new CustomException("??????????????????, ????????????");
    }

    long orderId = IdWorker.getId();

    AtomicInteger amount = new AtomicInteger(0);

    List<OrderDetail> orderDetails = shoppingCartList.stream().map((item) -> {
      OrderDetail orderDetail = new OrderDetail();
      orderDetail.setOrderId(orderId);
      orderDetail.setNumber(item.getNumber());
      orderDetail.setDishFlavor(item.getDishFlavor());
      orderDetail.setDishId(item.getDishId());
      orderDetail.setSetmealId(item.getSetmealId());
      orderDetail.setName(item.getName());
      orderDetail.setImage(item.getImage());
      orderDetail.setAmount(item.getAmount());
      amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
      return orderDetail;
    }).collect(Collectors.toList());

    orders.setId(orderId);
    orders.setOrderTime(LocalDateTime.now());
    orders.setCheckoutTime(LocalDateTime.now());
    orders.setStatus(2);
    orders.setAmount(new BigDecimal(amount.get()));//?????????
    orders.setUserId(currentId);
    orders.setNumber(String.valueOf(orderId));
    orders.setUserName(user.getName());
    orders.setConsignee(addressBook.getConsignee());
    orders.setPhone(addressBook.getPhone());
    orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
        + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
        + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
        + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));

    this.save(orders);

    //?????????????????????????????????????????????
    orderDetailService.saveBatch(orderDetails);

    //?????????????????????
    shoppingCartService.remove(queryWrapper);
  }
}
