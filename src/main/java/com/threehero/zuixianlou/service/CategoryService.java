package com.threehero.zuixianlou.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threehero.zuixianlou.pojo.Category;

public interface CategoryService extends IService<Category> {
  public void remove(Long id);
}
