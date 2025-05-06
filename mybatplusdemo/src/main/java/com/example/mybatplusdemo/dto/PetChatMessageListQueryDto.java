package com.example.mybatplusdemo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PetChatMessageListQueryDto {

    @ApiModelProperty("发送用户ID")
    private Long sendUserId;

    @ApiModelProperty("接受用户ID")
    private Long acceptUserId;

}
