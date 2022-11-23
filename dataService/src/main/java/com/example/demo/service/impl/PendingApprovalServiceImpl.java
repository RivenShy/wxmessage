package com.example.demo.service.impl;

import com.example.demo.entity.PendingApproval;
import com.example.demo.mapper.PendingApprovalMapper;
import com.example.demo.service.IPendingApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class PendingApprovalServiceImpl implements IPendingApprovalService {

    @Autowired
    PendingApprovalMapper pendingApprovalMapper;

    @Override
    public List<PendingApproval> list() {
        return pendingApprovalMapper.list();
    }

    @Override
    public PendingApproval getByUserCode(String userCode) {
        return pendingApprovalMapper.getByUserCode(userCode);
    }

    @Override
    public PendingApproval getAverageTime(Map<String, String> userCode) {
        return pendingApprovalMapper.getAverageTime(userCode);
    }

    @Override
    public List<PendingApproval> getRank() {
        return pendingApprovalMapper.getRank();
    }
}
