package com.example.mybatplusdemo.entity.websocket;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.mybatplusdemo.entity.PetBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "宠物-聊天接收者信息", description = "宠物-聊天接收者信息表")
@TableName(value ="PET_CHAT_ACCEPTER")
public class PetChatAccepter extends PetBaseEntity {

    @ApiModelProperty("发送用户ID")
    private Long acceptUserId;

    @ApiModelProperty("聊天消息ID")
    private Long chatMessageId;
}
