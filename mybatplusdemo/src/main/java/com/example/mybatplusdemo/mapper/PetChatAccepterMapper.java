package com.example.mybatplusdemo.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.mybatplusdemo.entity.websocket.PetChatAccepter;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PetChatAccepterMapper extends BaseMapper<PetChatAccepter> {
}
