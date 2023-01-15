package com.example.demo.mapper;

import com.example.demo.entity.MaterialHistory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MaterialHistoryMapper {
    List<MaterialHistory> selectMeterialHistoryPriceByCondition(String queryParam);
}
