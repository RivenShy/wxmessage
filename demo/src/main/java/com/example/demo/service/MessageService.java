package com.example.demo.service;

import com.example.demo.entity.Message;

import java.util.List;

public interface MessageService {

    public int add(Message message);

    public Message get(int id);

    public int updateClickTime(Message message);

    public List<Message> list();

    public int updateStatus(Message message);

    List<Message> listCondition(Message message);
}
