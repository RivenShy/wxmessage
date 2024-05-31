package com.example.mybatplusdemo.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    private String productId;// 商品ID
    private String subject;//订单名称
    private String body;// 商品描述
    private String totalFee;// 总金额
    private String outTradeNo;// 订单号(唯一)
    private Short payType;// 支付类型(1:支付宝 2:微信 )
    private Short payWay;// 支付方式 (1：app 2：公众号)

    private String name;
}