package com.example.demo.entity;

import lombok.Data;

import java.util.Date;

@Data
public class WxAccessToken {

    private String accessToken;

    private Date expireDate;
}
