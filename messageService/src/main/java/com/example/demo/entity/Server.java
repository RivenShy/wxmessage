package com.example.demo.entity;

import lombok.Data;

@Data
public class Server {

    private int id;

    private String serverName;

    private int customerId;

    private String serverUrl;

    private String serverIp;

    // 0为正常数据的值，1为删除数据的值
    private int deleted;

    //
    private String customerName;
    private String customerLogo;
}
