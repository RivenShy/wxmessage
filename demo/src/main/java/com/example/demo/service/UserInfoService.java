package com.example.demo.service;

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
    public List<UserInfo> list();
}
