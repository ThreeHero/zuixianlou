package com.threehero.zuixianlou.dto;

import com.threehero.zuixianlou.pojo.Setmeal;
import com.threehero.zuixianlou.pojo.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
