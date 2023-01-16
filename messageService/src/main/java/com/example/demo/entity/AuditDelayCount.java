package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class AuditDelayCount {

    private String jobuser;

    // 待审批单据总数
    private int adcount;

    // 已延期的单据总数
    private int delaycount;

    // todo for test
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dateTest;
}
