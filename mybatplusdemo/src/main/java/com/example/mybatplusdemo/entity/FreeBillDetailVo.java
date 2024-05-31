package com.example.mybatplusdemo.entity;

import lombok.Data;

import java.util.Date;

@Data
public class FreeBillDetailVo {

    private String houseName;

    private String oughtAmount;

    private String practicalAmount;

    private String oweAmount;

    private String period;

    private String remarks;

    private Date beginTime;

    private Date endTime;
}
