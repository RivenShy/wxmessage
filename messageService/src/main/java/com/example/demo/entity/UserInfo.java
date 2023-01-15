package com.example.demo.entity;

import lombok.Data;

@Data
public class UserInfo {

    private int id;

    private int serverId;

    private String userId;

    private String userName;

    private String phone;

    private String wxNickname;

    private String openId;

    private String wxNickname2;

    private String openId2;

    // 0为正常数据的值，1为删除数据的值
    private int deleted;

    private int consultantId;

    /**
     * 非数据库字段
     */
    private String serverName;

    private String customerName;

    private String serverIp;

    private String sign;

    // 实施顾问
    private String consultCode;

//    // guid
    private String guid;
}

