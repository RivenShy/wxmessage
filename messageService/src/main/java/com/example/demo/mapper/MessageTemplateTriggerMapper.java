package com.example.demo.mapper;

import com.example.demo.entity.ApprovalConfig;
import com.example.demo.entity.MessageTemplateTrigger;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageTemplateTriggerMapper {
    public List<MessageTemplateTrigger> selectByMessageTemplateId(int messageTemplateId);

    int add(MessageTemplateTrigger messageTemplateTriggerArgs);

    int deleteById(int id);

    List<MessageTemplateTrigger> selectListByCustomsql(MessageTemplateTrigger messageTemplateTrigger);
}
