package com.example.demo.mapper;

import com.example.demo.entity.Customer;
import com.example.demo.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysUserMapper {

    public SysUser findByUsername(String username);;

}