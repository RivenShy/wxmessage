package com.example.mybatplusdemo.dao;

import com.example.mybatplusdemo.entity.websocket.PetChatSender;
import com.example.mybatplusdemo.ma.base.PetBaseServiceImpl;
import com.example.mybatplusdemo.mapper.PetChatSenderMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PetChatSenderDaoImpl extends PetBaseServiceImpl<PetChatSenderMapper, PetChatSender> implements PetChatSenderDao {
}
