package com.example.demo.service.impl;

import com.example.demo.entity.Server;
import com.example.demo.mapper.ServerMapper;
import com.example.demo.service.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServerServiceImpl implements ServerService {

    @Autowired
    ServerMapper serverMapper;


    @Override
    public Server get(int id) {
        return serverMapper.get(id);
    }

    @Override
    public Server getServerByServerIp(String serverIp) {
        return serverMapper.getServerByServerIp(serverIp);
    }

    @Override
    public List<Server> list(int deleted) {
        return serverMapper.list(deleted);
    }

    @Override
    public int add(Server server) {
        return serverMapper.add(server);
    }

    @Override
    public int update(Server server) {
        return serverMapper.update(server);
    }

    @Override
    public List<Server> selectListByCustomerId(int customerId) {
        return serverMapper.selectListByCustomerId(customerId);
    }

    @Override
    public int removeById(int id) {
        return serverMapper.removeById(id);
    }
}
