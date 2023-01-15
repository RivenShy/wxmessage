package com.example.demo.entity;

import lombok.Data;
@Data
public class PendingApproval {

    // 用户代码
    private String jobuser;

//        待审/未审核数
    private int adcount;

//    今日已审核数
    private int totalCount;

    private String UserName;

    // 审批一张单据平均用了几分钟（平均时效）
    private int return_value;
}
