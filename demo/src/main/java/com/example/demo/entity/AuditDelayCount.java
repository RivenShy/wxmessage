package com.example.demo.entity;

import lombok.Data;

@Data
public class AuditDelayCount {

    private String jobuser;

    // 待审批数量
    private int adcount;

    // 已审批数量
    private int delaycount;
}
