package com.example.mybatplusdemo.service.websocket;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.mybatplusdemo.entity.websocket.ChatMessage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface MessageService {

    Map<String,Object> getChatMessage(String sendUserId,String acceptUserId, int pageNum, int pageSize);
//    IPage<ChatMessage> getChatMessage(String sendUserId,String acceptUserId, int pageNum, int pageSize);

    Map<String,Object> sendMsg(ChatMessage chatMessage);

    Map<String,Object> sendFileMsg(String sendUserId, String acceptUserId, String type, Integer time, MultipartFile file);

//    Map<String,Object> getFriendMsgList(String userId,int pageNum,int pageSize);

    IPage<ChatMessage> getFriendMsgList(String userId, int pageNum, int pageSize);


    Boolean readedMsg(String sendUserId,String acceptUserId);

    int getAllNoReadMsgNum(String userId);


}