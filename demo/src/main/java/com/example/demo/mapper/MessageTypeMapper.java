package com.example.demo.mapper;

import com.example.demo.entity.MessageType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageTypeMapper {

    public List<MessageType> list();

}
