package com.example.demo.service;

import com.example.demo.entity.UnbindUser;
import com.example.demo.entity.UserInfo;

import java.util.List;


public interface UserInfoService {
    public int add(UserInfo userInfo);
//
//    public void delete(int id);
//
    public UserInfo get(int id);

      public UserInfo getUserInfoByServerIdAndUserId(UserInfo userInfo);
//
    public int updateOpenIdAndNickName(UserInfo userInfo);

//    public int review(int id);
//
    public List<UserInfo> list(int deleted);

    public int removeById(int id);

    int updateUserNameById(UserInfo userInfo);

    int updateConsultById(UserInfo userInfo);

    List<UnbindUser> selectUnbindUserListByServerId(int serverId);

    UnbindUser selectUnbindUserByServerIdAndUserCode(UnbindUser unbindUserArgs);

    int addUnbindUser(UnbindUser unbindUserArgs);

    int deleteUnbindUserByServerIdAndUserId(UnbindUser unbindUserArgs);

    List<UserInfo> selectBindUserInfoByServerId(int serverId);

    List<UserInfo> selectUserInfoByOpenId(String openid);
}
