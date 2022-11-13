package com.threehero.zuixianlou.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.threehero.zuixianlou.common.R;
import com.threehero.zuixianlou.pojo.Category;
import com.threehero.zuixianlou.service.CategoryService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

  @Autowired
  private CategoryService categoryService;

  /**
   *  新增分类
   * @param category
   * @return
   */
  @PostMapping
  public R<String> save(@RequestBody Category category) {
    categoryService.save(category);
    return R.success("新增分类成功");
  }


  @GetMapping("/page")
  public R<Page> page(int page, int pageSize) {
    Page<Category> pageInfo = new Page<>(page, pageSize);

    LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.orderByAsc(Category::getSort);
    categoryService.page(pageInfo, queryWrapper);
    return R.success(pageInfo);
  }

  /**
   * 根据id删除分类
   * @param id
   * @return
   */
  @DeleteMapping
  public R<String> delete(Long id) {
    // categoryService.removeById(id);
    categoryService.remove(id);
    return R.success("分类信息查询成功");
  }

  /**
   * 修改分类信息
   * @param category
   * @return
   */
  @PutMapping
  public R<String> update(@RequestBody Category category) {
    categoryService.updateById(category);
    return R.success("修改分类信息成功");
  }

  @GetMapping("/list")
  public R<List<Category>> list(Category category) {
    // 条件构造器
    LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
    // 添加条件
    queryWrapper.eq(category.getType() != null, Category::getType, category.getType());
    // 添加排序条件
    queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

    List<Category> list = categoryService.list(queryWrapper);
    return R.success(list);

  }



}
