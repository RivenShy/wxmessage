package com.example.mybatplusdemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.mybatplusdemo.entity.websocket.ChatMessage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper extends BaseMapper<ChatMessage> {

//    List<ChatMessage> getFriendMsgList(String userId);

    List<ChatMessage> getFriendMsgList(IPage<ChatMessage> page, String userId);

//    List<ChatMessage> getChatMessage(String sendUserId,String acceptUserId);

    List<ChatMessage> getChatMessage(IPage<ChatMessage> page , String sendUserId,String acceptUserId);


    Boolean sendMsg(String sendUserId,String acceptUserId,String content,String type,Integer soundTime,String sendTime);

    Boolean readedMsg(String sendUserId,String acceptUserId);

    int getAllNoReadMsgNum(String userId);

    Boolean delMsg(int msgId);
}
