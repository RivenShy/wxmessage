package com.example.demo.mapper;

import com.example.demo.entity.ApprovalResult;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ApprovalResultMapper {
    List<ApprovalResult> list();
}
