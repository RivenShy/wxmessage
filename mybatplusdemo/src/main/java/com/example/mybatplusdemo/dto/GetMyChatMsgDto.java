package com.example.mybatplusdemo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GetMyChatMsgDto {

    @ApiModelProperty("目标用户")
    private Long userId;

    @ApiModelProperty("当前用户")
    private Long currentUserId;
}
