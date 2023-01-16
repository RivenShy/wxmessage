package com.example.demo.service;

import com.example.demo.entity.MessageType;
import com.example.demo.entity.UserInfo;
import org.springframework.stereotype.Service;

import java.util.List;


public interface MessageTypeService {

    public List<MessageType> list(int deleted);

    public MessageType get(int id);

    public int updateStatus(MessageType messageType);

    public int updateScheduleTimeById(MessageType messageTypeArgs);

    public int add(MessageType messageTypeArgs);

    int removeById(int id);
}
