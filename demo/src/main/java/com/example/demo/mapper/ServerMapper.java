package com.example.demo.mapper;

import com.example.demo.entity.Server;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ServerMapper {

    public Server get(int id);

    public Server getServerByServerIp(String serverIp);

    public List<Server> list();

    public int add(Server server);

    public int update(Server server);
}