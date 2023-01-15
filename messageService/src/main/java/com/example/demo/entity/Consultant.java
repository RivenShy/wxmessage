package com.example.demo.entity;

import lombok.Data;

@Data
public class Consultant {

    private int id;

    private String consultCode;

    private String consultName;

    private String wxNickName;

    private String openId;

    private int deleted;
}
