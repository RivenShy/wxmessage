package com.example.demo.mapper;

import com.example.demo.entity.ApprovalConfig;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ApprovalConfigMapper {
    public List<ApprovalConfig> list();

    ApprovalConfig selectByAuditName(String auditName);
}
