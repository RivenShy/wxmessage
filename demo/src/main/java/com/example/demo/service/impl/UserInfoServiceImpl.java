package com.example.demo.service.impl;

import com.example.demo.entity.UserInfo;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public UserInfo getUserInfoByServerIdAndUserId(UserInfo userInfo) {
        return userMapper.getUserInfoByServerIdAndUserId(userInfo);
    }

    @Override
    public int updateOpenIdAndNickName(UserInfo userInfo) {
        return userMapper.updateOpenIdAndNickName(userInfo);
    }

    @Override
    public List<UserInfo> list() {
        return userMapper.list();
    }

}
