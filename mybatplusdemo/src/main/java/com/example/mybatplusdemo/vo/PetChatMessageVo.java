package com.example.mybatplusdemo.vo;

import com.example.mybatplusdemo.entity.websocket.PetChatMessage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PetChatMessageVo extends PetChatMessage {

    @ApiModelProperty("发送用户名称")
    private String sendUserName;

    @ApiModelProperty("接收用户名称")
    private String acceptUserName;

    @ApiModelProperty("好友Id")
    private Long friendUserId;

    @ApiModelProperty("好友名称")
    private String friendUserName;

    @ApiModelProperty("未读消息数量")
    private Integer unReadNum;
}

