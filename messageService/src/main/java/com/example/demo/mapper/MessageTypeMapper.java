package com.example.demo.mapper;

import com.example.demo.entity.MessageType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageTypeMapper {

    public List<MessageType> list(@Param(value="deleted") int deleted);

    public MessageType get(int id);

    public int updateStatus(MessageType messageType);

    public int updateScheduleTimeById(MessageType messageTypeArgs);

    public int add(MessageType messageTypeArgs);

    int removeById(int id);
}
