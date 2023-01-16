package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ApprovalTimeout {

    // 物理主键
    private String codeid;

    // 提交时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date SubTime;

    // 审批名称
    private String AuditName;

    // 上一个审核时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastAuditTime;

    // 单据关键信息
    // 项目名称
    private String CodeDesc;
    //
    private String NumDesc;

    private String jobuser;
}
