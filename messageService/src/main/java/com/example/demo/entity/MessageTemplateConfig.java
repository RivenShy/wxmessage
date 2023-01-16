package com.example.demo.entity;

import lombok.Data;

@Data
public class MessageTemplateConfig {

    private int id;

    private int messageTemplateId;

    private String fieldName;

    private String fieldNameDesc;

    private String description;

    private int deleted;

    private String fieldDataType;

    private int alertField;
}
