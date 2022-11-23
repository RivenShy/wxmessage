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

    /**
     * 非数据库字段
     */
    private String serverName;

    private String customerName;

    private String serverIp;

    private String sign;
}

