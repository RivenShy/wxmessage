package com.example.demo.service.impl;

import com.example.demo.entity.ApprovalResultDetail;
import com.example.demo.mapper.ApprovalResultDetailMapper;
import com.example.demo.service.IApprovalResultDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApprovalResultDetailServiceImpl implements IApprovalResultDetailService {

    @Autowired
    private ApprovalResultDetailMapper approvalResultDetailMapper;

    @Override
    public List<ApprovalResultDetail> getByCodeId(String codeId) {
        return approvalResultDetailMapper.getByCodeId(codeId);
    }
}
