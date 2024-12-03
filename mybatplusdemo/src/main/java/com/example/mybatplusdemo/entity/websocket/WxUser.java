package com.example.mybatplusdemo.entity.websocket;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("wx_user")
public class WxUser {
    private Integer id;
    private String userId;
    private String nickName;
    private String headImg;
    private String phone;
    private String openid;
    private String unionid;
    private Integer status;
    private String createdTime;
    private String modefiedTime;
}