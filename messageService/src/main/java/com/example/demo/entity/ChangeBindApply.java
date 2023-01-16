package com.example.demo.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ChangeBindApply {

    private int id;

    private int serverId;

    private int userId;

    private String wxNickname;

    private String openId;

    private int status;

    private String operator;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date applyDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date reviewDate;

    private String userCode;

    // 0为正常数据的值，1为删除数据的值
    private int deleted;

    // 非数据库字段
    private String serverName;
    private String customerName;
    // 传参所需
    private String serverIp;
}
