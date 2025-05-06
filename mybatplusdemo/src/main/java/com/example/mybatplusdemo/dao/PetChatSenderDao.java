package com.example.mybatplusdemo.dao;

import com.example.mybatplusdemo.entity.websocket.PetChatSender;
import com.example.mybatplusdemo.ma.base.PetBaseService;
import com.example.mybatplusdemo.mapper.PetChatSenderMapper;

public interface PetChatSenderDao extends PetBaseService<PetChatSender> {

    default PetChatSenderMapper getMapper() {
        return (PetChatSenderMapper) this.getBaseMapper();
    }

}
