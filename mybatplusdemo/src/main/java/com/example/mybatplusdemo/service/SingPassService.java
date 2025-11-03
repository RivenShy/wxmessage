package com.example.mybatplusdemo.service;

import com.example.mybatplusdemo.vo.SingpassUserInfoVo;

public interface SingPassService {

    String generateLoginUrl();

    SingpassUserInfoVo handleCallback(String code, String state);
}
