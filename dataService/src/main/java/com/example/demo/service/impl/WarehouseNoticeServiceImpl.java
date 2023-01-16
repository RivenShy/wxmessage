package com.example.demo.service.impl;

import com.example.demo.entity.WarehouseNotice;
import com.example.demo.mapper.WarehouseNoticeMapper;
import com.example.demo.service.IWarehouseNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WarehouseNoticeServiceImpl implements IWarehouseNoticeService {

    @Autowired
    private WarehouseNoticeMapper warehouseNoticeMapper;

    @Override
    public WarehouseNotice getWarehouseNoticeByDocEntry(String docEntry) {
        return warehouseNoticeMapper.getWarehouseNoticeByDocEntry(docEntry);
    }
}
