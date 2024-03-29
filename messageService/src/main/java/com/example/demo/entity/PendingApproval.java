package com.example.demo.entity;

import lombok.Data;
@Data
public class PendingApproval {

    private String jobuser;

    //        待审/未审核数
    private int adcount;

    //    今日已审核数
    private int todayCount;

    private String jobuserName;

    private String UserName;

    // 审批一张单据平均用了几分钟,单独用，不与其它字段一起用
    private float return_value;
}
