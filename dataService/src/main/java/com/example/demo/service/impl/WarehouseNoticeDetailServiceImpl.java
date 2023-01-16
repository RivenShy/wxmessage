package com.example.demo.service.impl;

import com.example.demo.entity.WarehouseNotice;
import com.example.demo.entity.WarehouseNoticeDetail;
import com.example.demo.mapper.WarehouseNoticeDetailMapper;
import com.example.demo.mapper.WarehouseNoticeMapper;
import com.example.demo.service.IWarehouseNoticeDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarehouseNoticeDetailServiceImpl implements IWarehouseNoticeDetailService {

    @Autowired
    private WarehouseNoticeDetailMapper warehouseNoticeDetailMapper;

    @Override
    public List<WarehouseNoticeDetail> getWarehouseNoticeDetailByDocEntry(String docEntry) {
        return warehouseNoticeDetailMapper.getWarehouseNoticeDetailByDocEntry(docEntry);
    }
}
