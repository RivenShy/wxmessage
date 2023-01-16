package com.example.demo.service.impl;

import com.example.demo.entity.ApprovalResult;
import com.example.demo.mapper.ApprovalResultMapper;
import com.example.demo.service.IApprovalResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApprovalResultServiceImpl implements IApprovalResultService {

    @Autowired
    private ApprovalResultMapper approvalResultMapper;

    @Override
    public List<ApprovalResult> list() {
        return approvalResultMapper.list();
    }
}
