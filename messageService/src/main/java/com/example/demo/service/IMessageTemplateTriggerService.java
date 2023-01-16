package com.example.demo.service;

import com.example.demo.entity.ApprovalConfig;
import com.example.demo.entity.MessageTemplateTrigger;

import java.util.List;

public interface IMessageTemplateTriggerService {
    List<MessageTemplateTrigger> listByMessageTemplateId(int messageTemplateId);

    int add(MessageTemplateTrigger messageTemplateTriggerArgs);

    int deleteById(int id);

    List<MessageTemplateTrigger> selectListByCustomsql(MessageTemplateTrigger messageTemplateTrigger);
}
