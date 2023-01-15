package com.example.demo.entity;

import lombok.Data;

@Data
public class UnbindUser {

    private int id;

    private int serverId;

    private String userId;

    private String userName;

    private String serverName;

    private String customerName;

    private String serverIp;

    private String description;

    private int deleted;
}
