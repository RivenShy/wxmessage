package com.example.mybatplusdemo.dao;

import com.example.mybatplusdemo.entity.websocket.PetChatMessage;
import com.example.mybatplusdemo.ma.base.PetBaseServiceImpl;
import com.example.mybatplusdemo.mapper.PetChatMessageMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PetChatMessageDaoImpl extends PetBaseServiceImpl<PetChatMessageMapper, PetChatMessage> implements PetChatMessageDao {
}
