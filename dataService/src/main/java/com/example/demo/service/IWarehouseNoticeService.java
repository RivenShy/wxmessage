package com.example.demo.service;

import com.example.demo.entity.WarehouseNotice;

public interface IWarehouseNoticeService {

    WarehouseNotice getWarehouseNoticeByDocEntry(String docEntry);
}
