package com.example.demo.service;

import com.example.demo.entity.MessageTemplate;
import com.example.demo.entity.MessageTemplateConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IMessageTemplateService {

    public MessageTemplate get(int id);

    public MessageTemplate selectByTemplateName(String name);

    public List<MessageTemplate> list(int deleted);

    int update(MessageTemplate messageTemplate);

    int delete(int id);

    int add(MessageTemplate messageTemplate);

    List<MessageTemplateConfig> selectMessageTemplateConfigList(int messageTemplateId);

    List<MessageTemplateConfig> selectMessageTemplateConfigAlertField(int messageTemplateId);
}

