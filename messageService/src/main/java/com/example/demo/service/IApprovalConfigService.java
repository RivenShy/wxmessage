package com.example.demo.service;

import com.example.demo.entity.ApprovalConfig;
import java.util.List;

public interface IApprovalConfigService {

    public List<ApprovalConfig> list();

    ApprovalConfig selectByAuditName(String auditName);
}
