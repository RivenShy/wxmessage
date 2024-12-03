package com.example.mybatplusdemo.entity.websocket;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("chat_message")
public class ChatMessage {
    private Integer id;
    private String sendUserId;
    private String acceptUserId;
    private String type;
    private String content;
    private Integer soundTIme;
    private String sendTime;
    private Integer readedNum;
    private WxUser wxUser;
}