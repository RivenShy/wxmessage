package com.example.demo.mapper;

import com.example.demo.entity.Customer;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CustomerMapper {

    public Customer get(int id);

    public List<Customer> list();

    public int add(Customer customer);

    public int update(Customer customer);

    public int updateLogoPathById(Customer customer);
}