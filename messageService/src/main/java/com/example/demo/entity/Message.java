package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class Message {

    private int id;

    // 定时推送消息Id
    private int msgTypeId;

    private int status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date sendTime;

    private int userId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date clickTime;

    private String userName;

    private String wxNickname;

    // 0为正常数据的值，1为删除数据的值
    private int deleted;

    private int serverId;

    private String messageName;

    private int messageTemplateId;

    // 分页条件查询
    public Integer pageNum;
    public Integer pageSize;
}
