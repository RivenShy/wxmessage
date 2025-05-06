package com.example.mybatplusdemo.mapper;

import com.example.mybatplusdemo.entity.sharedingjdbc.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface OrderMapper {
    void insert(Order order);
    List<Order> selectByDate(@Param("date") Date date);
}
