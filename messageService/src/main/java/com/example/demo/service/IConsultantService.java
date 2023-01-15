package com.example.demo.service;

import com.example.demo.entity.Consultant;

import java.util.List;

public interface IConsultantService {

    public Consultant get(int id);

    public List<Consultant> list(int deleted);

    int add(Consultant consultant);

    int update(Consultant consultant);

    int delete(int id);

    Consultant selectByConsultCode(String consultCode);

    int checkIfExistConsultCode(Consultant consultant);
}
