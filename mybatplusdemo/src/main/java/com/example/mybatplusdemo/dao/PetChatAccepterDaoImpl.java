package com.example.mybatplusdemo.dao;

import com.example.mybatplusdemo.entity.websocket.PetChatAccepter;
import com.example.mybatplusdemo.ma.base.PetBaseServiceImpl;
import com.example.mybatplusdemo.mapper.PetChatAccepterMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PetChatAccepterDaoImpl extends PetBaseServiceImpl<PetChatAccepterMapper, PetChatAccepter> implements PetChatAccepterDao {

}
