package com.example.demo.service.impl;

import com.example.demo.entity.UnbindUser;
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
    public List<UserInfo> list(int deleted) {
        return userInfoMapper.list(deleted);
    }

    @Override
    public int removeById(int id) {
        return userInfoMapper.removeById(id);
    }

    @Override
    public int updateUserNameById(UserInfo userInfo) {
        return userInfoMapper.updateUserNameById(userInfo);
    }

    @Override
    public int updateConsultById(UserInfo userInfo) {
        return userInfoMapper.updateConsultById(userInfo);
    }

    @Override
    public int add(UserInfo userInfo) {
        return userInfoMapper.add(userInfo);
    }

    @Override
    public UserInfo get(int id) {
        return userInfoMapper.get(id);
    }

    @Override
    public List<UnbindUser> selectUnbindUserListByServerId(int serverId) {
        return userInfoMapper.selectUnbindUserListByServerId(serverId);
    }

    @Override
    public UnbindUser selectUnbindUserByServerIdAndUserCode(UnbindUser unbindUserArgs) {
        return userInfoMapper.selectUnbindUserByServerIdAndUserCode(unbindUserArgs);
    }

    @Override
    public int addUnbindUser(UnbindUser unbindUserArgs) {
        return userInfoMapper.addUnbindUser(unbindUserArgs);
    }

    @Override
    public int deleteUnbindUserByServerIdAndUserId(UnbindUser unbindUserArgs) {
        return userInfoMapper.deleteUnbindUserByServerIdAndUserId(unbindUserArgs);
    }

    @Override
    public List<UserInfo> selectBindUserInfoByServerId(int serverId) {
        return userInfoMapper.selectBindUserInfoByServerId(serverId);
    }

    @Override
    public List<UserInfo> selectUserInfoByOpenId(String openid) {
        return userInfoMapper.selectUserInfoByOpenId(openid);
    }
}
