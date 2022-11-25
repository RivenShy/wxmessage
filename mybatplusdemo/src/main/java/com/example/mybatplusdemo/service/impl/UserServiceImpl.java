package com.example.mybatplusdemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mybatplusdemo.mapper.UserMapper;
import com.example.mybatplusdemo.model.User;
import com.example.mybatplusdemo.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    private UserMapper userMapper;

//    //查询所有
//    @Override
//    public List<User> listAll() {
//        List<User> users = this.list();
//        return users;
//    }

    //条件查询
    public User listOne(Long id) {
        User user = this.getById(id);
        return user;
    }

    //保存
    public int insert1(User user){
        return userMapper.insert(user);
    }
}
