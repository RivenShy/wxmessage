package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class WarehouseNotice {

    // 项目名称
    private String ProjName;

    // 到货日期
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date DocDate;

    // 采购员
    private String ShopperName;

    // 供应商
    private String VedName;

    // 收货人账号
    private String StorePerId;

    // 收货人姓名
    private String StorePer;

    private String docEntry;
}
