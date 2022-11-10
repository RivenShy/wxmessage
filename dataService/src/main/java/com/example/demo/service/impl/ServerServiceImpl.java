package com.example.demo.service.impl;

import com.example.demo.entity.Server;
import com.example.demo.mapper.ServerMapper;
import com.example.demo.service.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServerServiceImpl implements ServerService {

    @Autowired
    ServerMapper serverMapper;


    @Override
    public Server get(int id) {
        return serverMapper.get(id);
    }


}
