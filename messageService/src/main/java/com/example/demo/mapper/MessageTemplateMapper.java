package com.example.demo.mapper;

import com.example.demo.entity.MessageTemplate;
import com.example.demo.entity.MessageTemplateConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface MessageTemplateMapper {

    public MessageTemplate get(int id);

    public MessageTemplate selectByTemplateName(String name);

    public List<MessageTemplate> list(@Param(value="deleted") int deleted);

    int update(MessageTemplate messageTemplate);

    int delete(int id);

    int add(MessageTemplate messageTemplate);

    List<MessageTemplateConfig> selectMessageTemplateConfigList(int messageTemplateId);

    List<MessageTemplateConfig> selectMessageTemplateConfigAlertField(int messageTemplateId);
}