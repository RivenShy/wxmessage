package com.example.demo.service.impl;

import com.example.demo.entity.UserInfo;
import com.example.demo.mapper.UserInfoMapper;
import com.example.demo.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Override
    public UserInfo getUserInfoByServerIdAndUserId(UserInfo userInfo) {
        return userInfoMapper.getUserInfoByServerIdAndUserId(userInfo);
    }

    @Override
    public int updateOpenIdAndNickName(UserInfo userInfo) {
        return userInfoMapper.updateOpenIdAndNickName(userInfo);
    }

    @Override
    public List<UserInfo> list() {
        return userInfoMapper.list();
    }

    @Override
    public int add(UserInfo userInfo) {
        return userInfoMapper.add(userInfo);
    }

    @Override
    public UserInfo get(int id) {
        return userInfoMapper.get(id);
    }

}
