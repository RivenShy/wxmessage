package com.example.demo.service;

import com.example.demo.entity.BindApply;

import java.util.List;


public interface BindApplyService {
    public int add(BindApply bindApply);
//
//    public void delete(int id);
//
    public BindApply get(int id);

    public List<BindApply> getUnRevewByServerIdAndUserId(BindApply bindApply);
//
//    public int update(Hero hero);

    public int review(int id);

    public List<BindApply> list();
}
