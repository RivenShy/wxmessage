package com.example.mybatplusdemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.mybatplusdemo.model.User;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.List;


public interface IUserService extends IService<User> {

    public List<User> selectAllIncludeDelete();

    public List<User> selectAllFromXml();
}
