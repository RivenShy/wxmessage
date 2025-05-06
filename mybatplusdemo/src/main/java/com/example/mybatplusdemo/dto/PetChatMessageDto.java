package com.example.mybatplusdemo.dto;

import com.example.mybatplusdemo.entity.websocket.PetChatMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PetChatMessageDto extends PetChatMessage {

}
