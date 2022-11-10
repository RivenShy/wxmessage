package com.example.demo.config;

import com.example.demo.entity.MessageType;
import com.example.demo.service.MessageTypeService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;

public class AccountServiceConfig implements ApplicationContextAware {

    private static MessageTypeService messageTypeService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        messageTypeService = applicationContext.getBean(MessageTypeService.class);
    }

    public static void initSchedule() {
        List<MessageType> list = messageTypeService.list();
        System.out.println(list);
    }

}
