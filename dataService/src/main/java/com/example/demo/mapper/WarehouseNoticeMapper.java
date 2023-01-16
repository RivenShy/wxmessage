package com.example.demo.mapper;

import com.example.demo.entity.WarehouseNotice;
import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface WarehouseNoticeMapper {

    WarehouseNotice getWarehouseNoticeByDocEntry(String docEntry);
}
