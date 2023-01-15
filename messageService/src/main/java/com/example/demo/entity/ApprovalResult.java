package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ApprovalResult {

    public static final String statusPass = "已通过";

    public static final String statusDeny = "否决";

    // 物理主键
    private String Codeid;

    // 提交人账号
    private String SubMitUser;

    // 提交时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date SubTime;

    // 审核状态：已通过、已否决
    private String Status;

    // 审批名称
    private String AuditName;

    // 审批完成时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date AuditTime;

    // 单据关键信息
    // 项目名称
    private String CodeDesc;
    // 供应商名称
    private String NumDesc;

    // 从表
    // 审批人姓名（角色）
    private String UserName;
    // 批语
    private String AuditMemo;
}
