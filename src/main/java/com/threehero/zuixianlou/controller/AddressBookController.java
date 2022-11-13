package com.threehero.zuixianlou.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.threehero.zuixianlou.common.BaseContext;
import com.threehero.zuixianlou.common.R;
import com.threehero.zuixianlou.pojo.AddressBook;
import com.threehero.zuixianlou.service.AddressBookService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Service
@RequestMapping("/addressBook")
@Slf4j
public class AddressBookController {

  @Autowired
  private AddressBookService addressBookService;

  /**
   * 新增地址
   * @param addressBook
   * @return
   */
  @PostMapping
  public R<AddressBook> save(@RequestBody AddressBook addressBook) {
    // 设置用户id
    addressBook.setUserId(BaseContext.getCurrentId());
    addressBookService.save(addressBook);
    return R.success(addressBook);
  }

  /**
   * 设置默认地址
   * @param addressBook
   * @return
   */
  @PutMapping("/default")
  private R<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
    LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
    wrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
    wrapper.set(AddressBook::getIsDefault, 0);
    addressBookService.update(wrapper);

    addressBook.setIsDefault(1);
    addressBookService.updateById(addressBook);
    return R.success(addressBook);
  }

  @PutMapping
  private R<String> update(@RequestBody AddressBook addressBook) {
    addressBookService.updateById(addressBook);
    return R.success("修改收货地址成功");
  }

  @DeleteMapping
  private R<String> delete(Long ids) {
    addressBookService.removeById(ids);
    return R.success("删除收货地址成功");
  }

  /**
   * 根据id查询地址
   */
  @GetMapping("/{id}")
  public R get(@PathVariable Long id) {
    AddressBook addressBook = addressBookService.getById(id);
    if (addressBook != null) {
      return R.success(addressBook);
    } else {
      return R.error("没有找到该对象");
    }
  }

  /**
   * 查询默认地址
   */
  @GetMapping("default")
  public R<AddressBook> getDefault() {
    LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
    queryWrapper.eq(AddressBook::getIsDefault, 1);

    //SQL:select * from address_book where user_id = ? and is_default = 1
    AddressBook addressBook = addressBookService.getOne(queryWrapper);

    if (null == addressBook) {
      return R.error("没有找到该对象");
    } else {
      return R.success(addressBook);
    }
  }

  /**
   * 查询指定用户的全部地址
   * @param addressBook
   * @return
   */
  @GetMapping("/list")
  public R<List<AddressBook>> list(AddressBook addressBook) {
    addressBook.setUserId(BaseContext.getCurrentId());
    log.info("addressBook:{}", addressBook);

    //条件构造器
    LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(null != addressBook.getUserId(), AddressBook::getUserId, addressBook.getUserId());
    queryWrapper.orderByDesc(AddressBook::getUpdateTime);

    //SQL:select * from address_book where user_id = ? order by update_time desc
    return R.success(addressBookService.list(queryWrapper));
  }

}
