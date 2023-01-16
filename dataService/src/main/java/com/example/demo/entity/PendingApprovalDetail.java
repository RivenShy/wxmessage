package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class PendingApprovalDetail {

    // 审批单据类型
    private String AuditName;

    // 项目名称
    private String ProjName;

    // 单据名称
    private String BillName;

    // 已过去的小时
    private float DelayHour;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastAuditTime;

}
