package com.example.mybatplusdemo.service.impl;

import com.example.mybatplusdemo.entity.sharedingjdbc.Order;
import com.example.mybatplusdemo.mapper.OrderMapper;
import com.example.mybatplusdemo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    public void createOrder(Order dto) {
        Order order = new Order();
        order.setId(System.currentTimeMillis());
        order.setUserId(123L);
        order.setOrderDate(dto.getOrderDate()); // 会根据日期分表
        orderMapper.insert(order);
    }

    @Override
    public List<Order> list(Order dto) {
        return orderMapper.selectByDate(null);
    }
}
