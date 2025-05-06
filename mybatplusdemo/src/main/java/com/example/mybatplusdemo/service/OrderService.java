package com.example.mybatplusdemo.service;

import com.example.mybatplusdemo.entity.sharedingjdbc.Order;

import java.util.List;

public interface OrderService {

    void createOrder(Order dto);

    List<Order> list(Order dto);
}
