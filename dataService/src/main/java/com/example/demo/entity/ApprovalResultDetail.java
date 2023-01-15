package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ApprovalResultDetail {

    // 审批阶段
    private String StageName;

    // 审批人姓名（角色）
    private String UserName;

    // 批语
    private String AuditMemo;

    // 审批时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date AuditTime;
}
