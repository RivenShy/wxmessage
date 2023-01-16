package com.example.demo.service.impl;

import com.example.demo.entity.ApprovalTimeout;
import com.example.demo.mapper.ApprovalTimeoutMapper;
import com.example.demo.service.IApprovalTimeoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApprovalTimeoutServiceImpl implements IApprovalTimeoutService {

    @Autowired
    private ApprovalTimeoutMapper approvalTimeoutMapper;

    @Override
    public List<ApprovalTimeout> list() {
        return approvalTimeoutMapper.list();
    }
}
