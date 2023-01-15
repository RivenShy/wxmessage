package com.example.demo.service.impl;

import com.example.demo.entity.MaterialHistory;
import com.example.demo.mapper.MaterialHistoryMapper;
import com.example.demo.service.IMaterialHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaterialHistoryServiceImpl implements IMaterialHistoryService {

    @Autowired
    private MaterialHistoryMapper materialHistoryMapper;

    @Override
    public List<MaterialHistory> selectMeterialHistoryPriceByCondition(String queryParam) {
        return materialHistoryMapper.selectMeterialHistoryPriceByCondition(queryParam);
    }
}
