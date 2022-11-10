package com.example.demo.mapper;

import com.example.demo.entity.BindApply;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BindApplyMapper {
    public int add(BindApply hero);

    public void delete(int id);

    public BindApply get(int id);

    public List<BindApply> getUnRevewByServerIdAndUserId(BindApply bindApply);

//    public int update(BindApply bindApply);

    public int review(int id);

    public List<BindApply> list();
}