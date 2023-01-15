package com.example.demo.entity;

import lombok.Data;

@Data
public class AuditDelayCount {

    // 用户代码
    private String jobuser;

    // 待审核数
    private int adcount;

    // 延迟审核数
    private int delaycount;
}
