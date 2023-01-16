package com.example.demo.service.impl;

import com.example.demo.entity.MessageTemplateTrigger;
import com.example.demo.mapper.MessageTemplateTriggerMapper;
import com.example.demo.service.IMessageTemplateTriggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageTemplateTriggerServiceImpl implements IMessageTemplateTriggerService {

    @Autowired
    private MessageTemplateTriggerMapper messageTemplateTriggerMapper;

    @Override
    public List<MessageTemplateTrigger> listByMessageTemplateId(int messageTemplateId) {
        return messageTemplateTriggerMapper.selectByMessageTemplateId(messageTemplateId);
    }

    @Override
    public int add(MessageTemplateTrigger messageTemplateTriggerArgs) {
        return messageTemplateTriggerMapper.add(messageTemplateTriggerArgs);
    }

    @Override
    public int deleteById(int id) {
        return messageTemplateTriggerMapper.deleteById(id);
    }

    @Override
    public List<MessageTemplateTrigger> selectListByCustomsql(MessageTemplateTrigger messageTemplateTrigger) {
        return messageTemplateTriggerMapper.selectListByCustomsql(messageTemplateTrigger);
    }
}
