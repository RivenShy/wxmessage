package com.example.demo.service.impl;

import com.example.demo.entity.MessageType;
import com.example.demo.mapper.MessageTypeMapper;
import com.example.demo.service.MessageTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageTypeServiceImpl implements MessageTypeService {

    @Autowired
    MessageTypeMapper messageTypeMapper;

    @Override
    public List<MessageType> list(int deleted) {
        return messageTypeMapper.list(deleted);
    }

    @Override
    public MessageType get(int id) {
        return messageTypeMapper.get(id);
    }

    @Override
    public int updateStatus(MessageType messageType) {
        return messageTypeMapper.updateStatus(messageType);
    }

    @Override
    public int updateScheduleTimeById(MessageType messageTypeArgs) {
        return messageTypeMapper.updateScheduleTimeById(messageTypeArgs);
    }

    @Override
    public int add(MessageType messageTypeArgs) {
        return messageTypeMapper.add(messageTypeArgs);
    }

    @Override
    public int removeById(int id) {
        return messageTypeMapper.removeById(id);
    }
}
