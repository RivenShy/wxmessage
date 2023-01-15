package com.security.demo.service;

import com.security.demo.config.ResponseResult;
import com.security.demo.entity.User;

public interface LoginServcie {
    ResponseResult login(User user);
    ResponseResult logout();
}
