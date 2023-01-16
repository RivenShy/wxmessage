package com.example.demo.service.impl;

import com.example.demo.entity.MessageTemplate;
import com.example.demo.entity.MessageTemplateConfig;
import com.example.demo.mapper.MessageTemplateMapper;
import com.example.demo.service.IMessageTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageTemplateServiceImpl implements IMessageTemplateService {

    @Autowired
    private MessageTemplateMapper messageTemplateMapper;

    @Override
    public MessageTemplate get(int id) {
        return messageTemplateMapper.get(id);
    }

    @Override
    public MessageTemplate selectByTemplateName(String name) {
        return messageTemplateMapper.selectByTemplateName(name);
    }

    @Override
    public List<MessageTemplate> list(int deleted) {
        return messageTemplateMapper.list(deleted);
    }

    @Override
    public int update(MessageTemplate messageTemplate) {
        return messageTemplateMapper.update(messageTemplate);
    }

    @Override
    public int delete(int id) {
        return messageTemplateMapper.delete(id);
    }

    @Override
    public int add(MessageTemplate messageTemplate) {
        return messageTemplateMapper.add(messageTemplate);
    }

    @Override
    public List<MessageTemplateConfig> selectMessageTemplateConfigList(int messageTemplateId) {
        return messageTemplateMapper.selectMessageTemplateConfigList(messageTemplateId);
    }

    @Override
    public List<MessageTemplateConfig> selectMessageTemplateConfigAlertField(int messageTemplateId) {
        return messageTemplateMapper.selectMessageTemplateConfigAlertField(messageTemplateId);
    }
}
