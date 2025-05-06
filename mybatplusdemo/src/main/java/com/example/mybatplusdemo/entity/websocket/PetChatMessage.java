package com.example.mybatplusdemo.entity.websocket;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.mybatplusdemo.entity.PetBaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("pet_chat_message")
public class PetChatMessage extends PetBaseEntity {

    @ApiModelProperty("发送用户ID")
    private Long sendUserId;

    @ApiModelProperty("接收用户ID")
    private Long acceptUserId;

    @ApiModelProperty("消息类型（0:文本,1: 图片,2:视频）")
    private Integer type;

    @ApiModelProperty("发送内容")
    private String content;

    @ApiModelProperty("是否阅读(0:否,1:是)")
    private Integer readed;

    @ApiModelProperty("音频时长")
    private Integer soundTime;
}
