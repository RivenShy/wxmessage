package com.example.demo.entity;

import lombok.Data;

@Data
public class WarehouseNoticeDetail {

    // 物料名称
    private String ItemName;

    // 品牌
    private String BandName;

    // 型号
    private String ModelNum;

    // 单位
    private String Unit;

    // 数量
    private int Qty;

    // 技术参数
    private String TechPramas;
}

