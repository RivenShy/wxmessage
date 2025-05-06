package com.example.mybatplusdemo.controller;

import com.example.mybatplusdemo.entity.sharedingjdbc.Order;
import com.example.mybatplusdemo.service.OrderService;
import com.example.mybatplusdemo.utils.R;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController()
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    @PostMapping("/save")
    public R<Boolean> createOrder(@RequestBody Order dto) {
        orderService.createOrder(dto);
        return R.success("");
    }

    @GetMapping("/list")
    public R<List<Order>> list(Order dto) {
        return R.data(orderService.list(dto));
    }
}
