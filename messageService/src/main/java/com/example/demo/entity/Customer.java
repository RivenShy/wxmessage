package com.example.demo.entity;


import lombok.Data;

@Data
public class Customer {

    private int id;

    private String customerCode;

    private String customerName;

    private String logoPath;

    // 0为正常数据的值，1为删除数据的值
    private int deleted;
}
