package com.example.mybatplusdemo.entity;

import lombok.Data;

@Data
public class HttpResult<T> {

    private int code;

    private boolean ok;

    private String message;

    private T data;

    private String description;
}
