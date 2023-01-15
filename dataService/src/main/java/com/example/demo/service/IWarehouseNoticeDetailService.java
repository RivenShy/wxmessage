package com.example.demo.service;

import com.example.demo.entity.WarehouseNotice;
import com.example.demo.entity.WarehouseNoticeDetail;

import java.util.List;

public interface IWarehouseNoticeDetailService {

    List<WarehouseNoticeDetail> getWarehouseNoticeDetailByDocEntry(String docEntry);
}
