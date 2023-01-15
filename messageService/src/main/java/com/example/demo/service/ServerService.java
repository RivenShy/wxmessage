package com.example.demo.service;

import com.example.demo.entity.Server;

import java.util.List;

public interface ServerService {

    public Server get(int id);

    public Server getServerByServerIp(String serverIp);

    public List<Server> list(int deleted);

    public int add(Server server);

    public int update(Server server);

    List<Server> selectListByCustomerId(int customerId);

    int removeById(int id);
}
