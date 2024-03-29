package com.example.demo.service.impl;
import com.example.demo.entity.Message;
import com.example.demo.mapper.MessageMapper;
import com.example.demo.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    MessageMapper messageMapper;

    @Override
    public int add(Message message) {
        return messageMapper.add(message);
    }

    @Override
    public Message get(int id) {
        return messageMapper.get(id);
    }

    @Override
    public int updateClickTime(Message message) {
        return messageMapper.updateClickTime(message);
    }

    @Override
    public List<Message> list(int deleted) {
        return messageMapper.list(deleted);
    }

    @Override
    public int updateStatus(Message message) {
        return messageMapper.updateStatus(message);
    }

    @Override
    public List<Message> listCondition(Message message) {
        return messageMapper.listCondition(message);
    }

    @Override
    public int removeById(int id) {
        return messageMapper.removeById(id);
    }

}
