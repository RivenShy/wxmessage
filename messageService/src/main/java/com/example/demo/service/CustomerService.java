package com.example.demo.service;

import com.example.demo.entity.Customer;

import java.util.List;

public interface CustomerService {

    public Customer get(int id);

    public List<Customer> list(int deleted);

    public int add(Customer customer);

    public int update(Customer customer);

    public int updateLogoPathById(Customer customer);

    int removeById(int id);

    Customer getByCustomerName(String customerName);
}
