package com.example.demo.mapper;

import com.example.demo.entity.UserDepm;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserDepmMapper {
//    public int add(BindApply hero);
//
//    public void delete(int id);
//
//    public BindApply get(int id);
//
//    public List<BindApply> getUnRevewByID(int id);

//    public int update(BindApply bindApply);

//    public int review(int id);

    public List<UserDepm> list();
}