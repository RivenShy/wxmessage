package com.example.demo.mapper;

import com.example.demo.entity.UnbindUser;
import com.example.demo.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserInfoMapper {

    public int updateOpenIdAndNickName(UserInfo userInfo);

    public UserInfo getUserInfoByServerIdAndUserId(UserInfo userInfo);

    public List<UserInfo> list(@Param(value="deleted") int deleted);

    public UserInfo get(int id);

    public int add(UserInfo userInfo);

    public int removeById(int deleted);

    int updateUserNameById(UserInfo userInfo);

    int updateConsultById(UserInfo userInfo);

    List<UnbindUser> selectUnbindUserListByServerId(int serverId);

    UnbindUser selectUnbindUserByServerIdAndUserCode(UnbindUser unbindUserArgs);

    int addUnbindUser(UnbindUser unbindUserArgs);

    int deleteUnbindUserByServerIdAndUserId(UnbindUser unbindUserArgs);

    List<UserInfo> selectBindUserInfoByServerId(int serverId);

    List<UserInfo> selectUserInfoByOpenId(String openid);
}
