package com.example.mybatplusdemo.config.websocket;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("消息发送dto(分聊天消息还是通知消息)")
public class MessageSendInfoDto<T> implements Serializable {

    @ApiModelProperty("消息种类")
    private Integer msgKind;

    @ApiModelProperty("消息data")
    private T msgData;
}
