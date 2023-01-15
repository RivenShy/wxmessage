package com.example.demo.service;

import com.example.demo.entity.ApprovalResultDetail;

import java.util.List;

public interface IApprovalResultDetailService {
    List<ApprovalResultDetail> getByCodeId(String codeId);
}
