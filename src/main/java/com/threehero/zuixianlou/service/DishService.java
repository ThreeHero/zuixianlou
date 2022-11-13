package com.threehero.zuixianlou.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threehero.zuixianlou.dto.DishDto;
import com.threehero.zuixianlou.pojo.Dish;
import java.util.List;

public interface DishService extends IService<Dish> {

  void saveWithFlavor(DishDto dishDto);

  DishDto getByIdWithFlavor(Long id);

  void updateWithFlavor(DishDto dishDto);

  void removeWithFlavor(List<Long> ids);

  // void removeByIdWithFlavor(Long ids);
}
