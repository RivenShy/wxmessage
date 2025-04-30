package com.example.mybatplusdemo.config.websocket;

import com.example.mybatplusdemo.entity.websocket.ChatMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChatMsgSendDto extends ChatMessage {

}
