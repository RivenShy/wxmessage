package com.example.demo.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MaterialHistory {

//    物料编码
    private String ItemCode;

//    物料名称
    private String ItemName;

//    品牌
    private String BandName;

//    型号
    private String ModelNum;

//    单位
    private String Unit;

//    最高价格
    private BigDecimal MaxPrice;

//    最低价格
    private BigDecimal MinPrice;
}
