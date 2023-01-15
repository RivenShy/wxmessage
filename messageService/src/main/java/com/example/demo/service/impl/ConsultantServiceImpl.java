package com.example.demo.service.impl;

import com.example.demo.entity.Consultant;
import com.example.demo.entity.Customer;
import com.example.demo.mapper.ConsultantMapper;
import com.example.demo.service.IConsultantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultantServiceImpl implements IConsultantService {

    @Autowired
    private ConsultantMapper consultantMapper;

    @Override
    public Consultant get(int id) {
        return consultantMapper.get(id);
    }

    @Override
    public List<Consultant> list(int deleted) {
        return consultantMapper.list(deleted);
    }

    @Override
    public int add(Consultant consultant) {
        return consultantMapper.add(consultant);
    }

    @Override
    public int update(Consultant consultant) {
        return consultantMapper.update(consultant);
    }

    @Override
    public int delete(int id) {
        return consultantMapper.delete(id);
    }

    @Override
    public Consultant selectByConsultCode(String consultCode) {
        return consultantMapper.selectByConsultCode(consultCode);
    }

    @Override
    public int checkIfExistConsultCode(Consultant consultant) {
        return consultantMapper.checkIfExistConsultCode(consultant);
    }
}
