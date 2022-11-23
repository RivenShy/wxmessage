package com.example.demo.service.impl;

import com.example.demo.entity.PendingApproval;
import com.example.demo.entity.PendingApprovalDetail;
import com.example.demo.mapper.PendingApprovalDetailMapper;
import com.example.demo.mapper.PendingApprovalMapper;
import com.example.demo.service.IPendingApprovalDetailService;
import com.example.demo.service.IPendingApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PendingApprovalDetailServiceImpl implements IPendingApprovalDetailService {

    @Autowired
    PendingApprovalDetailMapper pendingApprovalDetailMapper;

    @Override
    public List<PendingApprovalDetail> listByUserCode(Map<String, String> userCode) {
        return pendingApprovalDetailMapper.listByUserCode(userCode);
    }
}
