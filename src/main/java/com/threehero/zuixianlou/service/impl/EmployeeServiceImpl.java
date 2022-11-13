package com.threehero.zuixianlou.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threehero.zuixianlou.mapper.EmployeeMapper;
import com.threehero.zuixianlou.pojo.Employee;
import com.threehero.zuixianlou.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

}
