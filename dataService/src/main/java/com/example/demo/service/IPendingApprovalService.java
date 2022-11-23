package com.example.demo.service;

import com.example.demo.entity.PendingApproval;

import java.util.List;
import java.util.Map;


public interface IPendingApprovalService {

    public List<PendingApproval> list();

    PendingApproval getByUserCode(String userCode);

    PendingApproval getAverageTime(Map<String, String> userCode);

    List<PendingApproval> getRank();
}
