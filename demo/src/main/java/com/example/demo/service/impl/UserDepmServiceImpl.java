package com.example.demo.service.impl;

import com.example.demo.entity.BindApply;
import com.example.demo.entity.UserDepm;
import com.example.demo.mapper.BindApplyMapper;
import com.example.demo.mapper.UserDepmMapper;
import com.example.demo.service.BindApplyService;
import com.example.demo.service.UserDepmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDepmServiceImpl implements UserDepmService {

    @Autowired
    UserDepmMapper userDepmMapper;
//
//    @Override
//    public int add(BindApply bindApply) {
//        return bindApplyMapper.add(bindApply);
//    }
//
//    @Override
//    public void delete(int id) {
//
//    }
//
//    @Override
//    public BindApply get(int id) {
//        return bindApplyMapper.get(id);
//    }
//
//    @Override
//    public List<BindApply> getUnRevewByID(int id) {
//        return bindApplyMapper.getUnRevewByID(id);
//    }
//
//    @Override
//    public int update(Hero hero) {
//        return 0;
//    }

//    @Override
//    public int review(int id) {
//        return bindApplyMapper.review(id);
//    }

    @Override
    public List<UserDepm> list() {
        return userDepmMapper.list();
    }
}
