package com.example.demo.service;

import com.example.demo.entity.PendingApproval;
import com.example.demo.entity.PendingApprovalDetail;

import java.util.List;
import java.util.Map;


public interface IPendingApprovalDetailService {

    public List<PendingApprovalDetail> listByUserCode(Map<String, String> userCode);
}
