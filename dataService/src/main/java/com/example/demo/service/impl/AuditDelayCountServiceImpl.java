package com.example.demo.service.impl;

import com.example.demo.entity.AuditDelayCount;
import com.example.demo.mapper.AuditDelayCountMapper;
import com.example.demo.service.IAuditDelayCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditDelayCountServiceImpl implements IAuditDelayCountService {

    @Autowired
    AuditDelayCountMapper auditDelayCountMapper;

    @Override
    public List<AuditDelayCount> list() {
        return auditDelayCountMapper.list();
    }

    @Override
    public AuditDelayCount getByUserCode(String userCode) {
        return auditDelayCountMapper.getByUserCode(userCode);
    }
}
