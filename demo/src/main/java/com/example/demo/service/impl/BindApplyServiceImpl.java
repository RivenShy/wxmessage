package com.example.demo.service;

import com.example.demo.entity.Hero;

import java.util.List;


public interface BindApplyService {
    public int add(Hero hero);

    public void delete(int id);

    public Hero get(int id);

    public int update(Hero hero);

    public List<Hero> list();
}
