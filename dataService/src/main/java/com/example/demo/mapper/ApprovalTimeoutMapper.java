package com.example.demo.mapper;

import com.example.demo.entity.ApprovalTimeout;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ApprovalTimeoutMapper {
    List<ApprovalTimeout> list();
}
