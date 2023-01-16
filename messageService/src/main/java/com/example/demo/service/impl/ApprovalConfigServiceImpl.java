package com.example.demo.service.impl;

import com.example.demo.entity.ApprovalConfig;
import com.example.demo.mapper.ApprovalConfigMapper;
import com.example.demo.service.IApprovalConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApprovalConfigServiceImpl implements IApprovalConfigService {

    @Autowired
    private ApprovalConfigMapper approvalConfigMapper;

    @Override
    public List<ApprovalConfig> list() {
        return approvalConfigMapper.list();
    }

    @Override
    public ApprovalConfig selectByAuditName(String auditName) {
        return approvalConfigMapper.selectByAuditName(auditName);
    }

}
