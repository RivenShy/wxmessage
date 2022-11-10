package com.example.demo.service;

import com.example.demo.entity.MessageType;
import com.example.demo.entity.UserInfo;
import org.springframework.stereotype.Service;

import java.util.List;


public interface MessageTypeService {

    public List<MessageType> list();
}
