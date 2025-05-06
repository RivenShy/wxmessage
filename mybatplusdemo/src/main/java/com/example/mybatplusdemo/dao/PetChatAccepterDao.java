package com.example.mybatplusdemo.dao;

import com.example.mybatplusdemo.entity.websocket.PetChatAccepter;
import com.example.mybatplusdemo.ma.base.PetBaseService;
import com.example.mybatplusdemo.mapper.PetChatAccepterMapper;

public interface PetChatAccepterDao extends PetBaseService<PetChatAccepter> {

    default PetChatAccepterMapper getMapper() {
        return (PetChatAccepterMapper) this.getBaseMapper();
    }
}
