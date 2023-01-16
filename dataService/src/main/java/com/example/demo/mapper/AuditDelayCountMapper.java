package com.example.demo.mapper;

import com.example.demo.entity.AuditDelayCount;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AuditDelayCountMapper {

    public List<AuditDelayCount> list();

    AuditDelayCount getByUserCode(String userCode);
}
