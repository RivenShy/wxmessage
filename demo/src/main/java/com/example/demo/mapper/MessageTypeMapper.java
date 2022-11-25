package com.example.demo.mapper;

import com.example.demo.entity.MessageType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageTypeMapper {

    public List<MessageType> list();

    public MessageType get(int id);

    public int updateStatus(MessageType messageType);

    public int updateScheduleTimeById(MessageType messageTypeArgs);

    public int add(MessageType messageTypeArgs);
}
