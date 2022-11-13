package com.threehero.zuixianlou.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threehero.zuixianlou.mapper.UserMapper;
import com.threehero.zuixianlou.pojo.User;
import com.threehero.zuixianlou.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
