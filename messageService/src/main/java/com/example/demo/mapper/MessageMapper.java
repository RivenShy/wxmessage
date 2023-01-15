package com.example.demo.mapper;

import com.example.demo.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageMapper {

    public int add(Message message);

    public Message get(int id);

    public int updateClickTime(Message message);

    public List<Message> list(@Param(value="deleted") int deleted);

    public int updateStatus(Message message);

    List<Message> listCondition(@Param("message") Message message);

    int removeById(int id);
}