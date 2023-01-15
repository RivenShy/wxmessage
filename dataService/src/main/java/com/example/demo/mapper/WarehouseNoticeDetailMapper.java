package com.example.demo.mapper;

import com.example.demo.entity.WarehouseNotice;
import com.example.demo.entity.WarehouseNoticeDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WarehouseNoticeDetailMapper {

    List<WarehouseNoticeDetail> getWarehouseNoticeDetailByDocEntry(String docEntry);
}
