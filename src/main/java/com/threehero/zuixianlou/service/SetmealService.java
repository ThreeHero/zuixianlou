package com.threehero.zuixianlou.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threehero.zuixianlou.dto.SetmealDto;
import com.threehero.zuixianlou.pojo.Setmeal;
import java.util.List;

public interface SetmealService extends IService<Setmeal> {
  void saveWithDish(SetmealDto setmealDto);

  SetmealDto getByIdWithDish(Long id);

  void updateWithDish(SetmealDto setmealDto);

  void removeWithDish(List<Long> ids);
}
