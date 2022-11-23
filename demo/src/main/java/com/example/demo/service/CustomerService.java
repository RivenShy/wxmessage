package com.example.demo.service;

import com.example.demo.entity.Customer;

import java.util.List;

public interface CustomerService {

    public Customer get(int id);

    public List<Customer> list();

    public int add(Customer customer);

    public int update(Customer customer);
}
