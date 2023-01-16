package com.example.demo.controller.rocketmq;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.config.ScheduleConfig;
import com.example.demo.controller.MessageTemplateController;
import com.example.demo.controller.MessageTypeController;
import com.example.demo.entity.*;
import com.example.demo.result.R;
import com.example.demo.result.ResultCode;
import com.example.demo.service.*;
import com.example.demo.util.OkHttpUtil;
import com.example.demo.util.WxUtil;
import com.github.pagehelper.StringUtil;
import org.apache.log4j.Logger;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

//@RocketMQMessageListener(topic = "java1234-rocketmq",consumerGroup ="${rocketmq.consumer.group}" )
//@Component
//public class ConsumerService implements RocketMQListener<TestMessaging> {
//public class ConsumerService implements RocketMQListener<MessageExt> {
    public class ConsumerService implements RocketMQListener<String> {
//public class ConsumerService implements RocketMQListener<MessageTemplate> {
    private static Logger logger = Logger.getLogger(MessageTypeController.class);


//    @Override
//    public void onMessage(String s) {
//        System.out.println("收到消息内容："+s);
//    }

//    @Override
//    public void onMessage(MessageTemplate messageTemplate) {
//        System.out.println("收到消息内容："+ messageTemplate);
//        commonSendMessageMethod(messageTemplate);
//    }

    @Override
    public void onMessage(String str) {
        System.out.println("收到消息内容："+ str);
        JSONObject jsonObject = JSONObject.parseObject(str);
        String serverIp = jsonObject.getString("serverIp");
        String masterTableUniqueKey = jsonObject.getString("masterTableUniqueKey");
        String slaveTableUniqueKey = jsonObject.getString("slaveTableUniqueKey");
        int messageTemplateId = jsonObject.getInteger("messageTemplateId");
        String userCode = jsonObject.getString("userCode");
        System.out.println("serverIp = "+ serverIp);
        System.out.println("masterTableUniqueKey = "+ masterTableUniqueKey);
        System.out.println("slaveTableUniqueKey = "+ slaveTableUniqueKey);
        System.out.println("messageTemplateId = "+ messageTemplateId);
        System.out.println("userCode = "+ userCode);
        MessageTemplate messageTemplate = new MessageTemplate();
        messageTemplate.setServerIp(serverIp);
        messageTemplate.setId(messageTemplateId);
        messageTemplate.setMasterTableUniqueKey(masterTableUniqueKey);
        MessageTemplateController.handleCommonSendMessage(messageTemplate);
    }

//    @Override
//    public String onMessage(MessageExt messageExt) {
//        System.out.println("收到消息内容："+ messageExt);
//        return null;
//    }

//    @Override
//    public void onMessage(TestMessaging testMessaging) {
//        System.out.println("收到消息内容："+ testMessaging);
//    }
}