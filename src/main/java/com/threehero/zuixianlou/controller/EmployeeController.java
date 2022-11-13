package com.threehero.zuixianlou.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.threehero.zuixianlou.common.R;
import com.threehero.zuixianlou.pojo.Employee;
import com.threehero.zuixianlou.service.EmployeeService;
import java.time.LocalDateTime;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

  @Autowired
  private EmployeeService employeeService;

  /**
   * 员工登录
   * @param request
   * @param employee
   * @return
   */
  @PostMapping("/login")
  // @CrossOrigin
  public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
    // 将密码 进行 md5 加密处理
    String password = employee.getPassword();
    password = DigestUtils.md5DigestAsHex(password.getBytes());

    // 根据提交过来的用户名查询数据库
    LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(Employee::getUsername, employee.getUsername());
    Employee emp = employeeService.getOne(queryWrapper);

    // 没有查询到 返回登录失败
    if (emp == null) {
      return R.error("登录失败，用户名错误");
    }

    // 密码比对 不一样返回登录失败
    if (!emp.getPassword().equals(password)) {
      return R.error("登录失败，密码错误");
    }

    // 查看员工状态 是否被禁用 如果被禁用返回登录失败 0 禁用 1 可用
    if (emp.getStatus() == 0) {
      return R.error("登录失败，该用户被禁用");
    }

    // 登录成功  将员工id放入session 并返回登录成功
    request.getSession().setAttribute("employee", emp.getId());
    return R.success(emp);
  }

  /**
   * 退出登录
   * @param request
   * @return
   */
  @PostMapping("/logout")
  public R<String> logout(HttpServletRequest request) {
    request.getSession().removeAttribute("employee");
    return R.success("退出成功");
  }

  /**
   * 新增员工
   * @param employee
   * @return
   */
  @PostMapping
  public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
    log.info("新增员工，员工信息：{}", employee.toString());
    // 设置基础密码 123456
    employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
    // employee.setCreateTime(LocalDateTime.now());
    // employee.setUpdateTime(LocalDateTime.now());
    //
    // Long empId = (Long) request.getSession().getAttribute("employee");
    // employee.setCreateUser(empId);
    // employee.setUpdateUser(empId);

    employeeService.save(employee);

    return R.success("新增员工成功");
  }

  /**
   * 员工信息分页查询
   * @param page
   * @param pageSize
   * @param name
   * @return
   */
  @GetMapping("/page")
  public R<Page> page(int page, int pageSize, String name) {
    Page<Employee> pageInfo = new Page<>(page, pageSize);

    LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
    queryWrapper.orderByDesc(Employee::getUpdateTime);

    employeeService.page(pageInfo, queryWrapper);

    return R.success(pageInfo);
  }

  /**
   * 根据id修改员工信息
   * @param employee
   * @return
   */
  @PutMapping
  public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
    employee.setUpdateTime(LocalDateTime.now());

    long id = Thread.currentThread().getId();
    log.info("id: {}", id);
    // Long empId = (Long) request.getSession().getAttribute("employee");
    // employee.setUpdateUser(empId);
    employeeService.updateById(employee);
    return R.success("员工信息修改成功");
  }

  /**
   * 根据id查询员工信息
   * @param id
   * @return
   */
  @GetMapping("/{id}")
  public R<Employee> getById(@PathVariable Long id) {
    Employee employee = employeeService.getById(id);
      if (employee != null) {
      return R.success(employee);
    }
    return R.error("没有查询到此员工");
  }
}
