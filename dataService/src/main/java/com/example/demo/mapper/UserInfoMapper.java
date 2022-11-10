package com.example.demo.mapper;

import com.example.demo.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserInfoMapper {

    public int updateOpenIdAndNickName(UserInfo userInfo);

    public UserInfo getUserInfoByServerIdAndUserId(UserInfo userInfo);

    public List<UserInfo> list();

    public UserInfo get(int id);

}
