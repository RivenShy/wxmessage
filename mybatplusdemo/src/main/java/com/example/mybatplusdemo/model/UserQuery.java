package com.example.mybatplusdemo.model;

import lombok.Data;

@Data
public class UserQuery extends User {
    //用作年龄的上限
    private Integer age2;
}
