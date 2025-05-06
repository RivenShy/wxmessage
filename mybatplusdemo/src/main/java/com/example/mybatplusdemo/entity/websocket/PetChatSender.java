package com.example.mybatplusdemo.entity.websocket;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.mybatplusdemo.entity.PetBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "宠物-聊天发送者信息表", description = "宠物-聊天发送者信息")
@TableName(value ="PET_CHAT_SENDER")
public class PetChatSender extends PetBaseEntity {

    @ApiModelProperty("发送用户ID")
    private Long sendUserId;

    @ApiModelProperty("聊天消息ID")
    private Long chatMessageId;
}