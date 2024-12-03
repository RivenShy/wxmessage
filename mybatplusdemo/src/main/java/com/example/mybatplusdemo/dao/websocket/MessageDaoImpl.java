package com.example.mybatplusdemo.dao.websocket;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mybatplusdemo.entity.websocket.ChatMessage;
import com.example.mybatplusdemo.mapper.MessageMapper;
import org.springframework.stereotype.Repository;

@Repository
public class MessageDaoImpl extends ServiceImpl<MessageMapper, ChatMessage> implements MessageDao {
}
