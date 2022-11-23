package com.example.demo.service.impl;

import com.example.demo.entity.Customer;
import com.example.demo.mapper.CustomerMapper;
import com.example.demo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    CustomerMapper customerMapper;


    @Override
    public Customer get(int id) {
        return customerMapper.get(id);
    }

    @Override
    public List<Customer> list() {
        return customerMapper.list();
    }

    @Override
    public int add(Customer customer) {
        return customerMapper.add(customer);
    }

    @Override
    public int update(Customer customer) {
        return customerMapper.update(customer);
    }


}
