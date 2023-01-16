package com.example.demo.mapper;

import com.example.demo.entity.BindApply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BindApplyMapper {
    public int add(BindApply bndApply);

    public void delete(int id);

    public BindApply get(int id);

    public BindApply getUnRevewByServerIdAndUserId(BindApply bindApply);

//    public int update(BindApply bindApply);

    public int review(int id);

    public List<BindApply> list(@Param(value="deleted") int deleted);

    public int deleteByServerIdAndUserCode(BindApply bindApplyArgs);

    public int reBindByServerIdAndUserId(BindApply bindApplyArgs);

    int removeById(int id);

    BindApply getUnRevewByServerIdAndUserName(BindApply bindApplyCondition);
}