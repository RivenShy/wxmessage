package com.example.mybatplusdemo.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.mybatplusdemo.entity.websocket.PetChatSender;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PetChatSenderMapper extends BaseMapper<PetChatSender> {
}
