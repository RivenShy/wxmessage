package com.example.demo.entity;

import lombok.Data;

@Data
public class Server {

    private int id;

    private String serverName;

    private int customerId;

    private String serverUrl;

    private String serverIp;
}
