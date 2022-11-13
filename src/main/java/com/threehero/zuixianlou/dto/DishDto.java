package com.threehero.zuixianlou.dto;

import com.threehero.zuixianlou.pojo.Dish;
import com.threehero.zuixianlou.pojo.DishFlavor;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
