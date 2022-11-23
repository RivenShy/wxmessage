package com.example.demo.entity;

import lombok.Data;

import lombok.Data;

@Data
public class PendingApprovalDetail {

    // 审批单据类型
    private String AuditName;

    // 项目名称
    private String ProjName;

    // 单据名称
    private String BillName;

    // 已过去的小时
    private int DelayHour;

}
