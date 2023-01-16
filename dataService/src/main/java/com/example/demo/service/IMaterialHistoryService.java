package com.example.demo.service;

import com.example.demo.entity.MaterialHistory;

import java.util.List;

public interface IMaterialHistoryService {
    List<MaterialHistory> selectMeterialHistoryPriceByCondition(String queryParam);
}
