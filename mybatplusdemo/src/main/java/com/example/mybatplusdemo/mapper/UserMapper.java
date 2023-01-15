package com.example.mybatplusdemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.mybatplusdemo.model.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {

    @Select("select * from user")
    List<User> selectAllIncludeDelete();

    List<User> selectAllFromXml();
}