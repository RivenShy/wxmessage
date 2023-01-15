package com.example.demo.controller.rocketmq;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.MessageTemplate;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

//@RestController("producerService")
//@RequestMapping("/producerService")
public class ProducerService {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 发送简单消息
     */
//    @GetMapping("/sendMessage")
    public void sendMessage(){
        for(int i=0;i<1000;i++){
            rocketMQTemplate.convertAndSend("java1234-rocketmq","rocketmq大爷，你好！"+i);
        }
    }

//    @GetMapping("/sendMessage2")
    public void sendMessage2(){
        MessageTemplate messageTemplate = new MessageTemplate();
        messageTemplate.setTemplateName("入库通知");
        rocketMQTemplate.convertAndSend("java1234-rocketmq", messageTemplate);
    }

//    @GetMapping("/sendMessage3")
    public void sendMessage3(){
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("templateName", "审核结果通知");
//        MessageExt messageExt = new MessageExt();
//        messageExt.setBody("审核结果通知".getBytes());

//        TestMessaging testMessaging = new TestMessaging()
//                .setMsgId(UUID.randomUUID().toString())
//                .setMsgText("审核结果通知");
//        rocketMQTemplate.convertAndSend("java1234-rocketmq", testMessaging);
    }

//    @PostMapping("/sendMessage4")
    public void sendMessage4(@RequestBody MessageTemplate messageTemplate){
        rocketMQTemplate.convertAndSend("java1234-rocketmq", messageTemplate);
    }

//    @PostMapping("/sendMessage5")
    public void sendMessage5(@RequestBody MessageTemplate messageTemplate){
        rocketMQTemplate.convertAndSend("java1234-rocketmq", messageTemplate);
    }

//    @PostMapping("/sendMessage6")
    public void sendMessage6(@RequestBody MessageTemplate messageTemplate){
        // 同步发送消息
        rocketMQTemplate.convertAndSend("java1234-rocketmq", "Hello, World sync!");
    }

//    @PostMapping("/sendMessage7")
    public void sendMessage7(@RequestBody MessageTemplate messageTemplate){
        //send spring message
        rocketMQTemplate.send("java1234-rocketmq", MessageBuilder.withPayload("Hello, World! I'm from spring message").build());
    }

//    @PostMapping("/sendMessage8")
    public void sendMessage8(@RequestBody MessageTemplate messageTemplate){
        // 异步发送消息
        rocketMQTemplate.asyncSend("java1234-rocketmq", "Hello, World async!", new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.printf("async onSucess SendResult=%s %n", sendResult);
            }

            @Override
            public void onException(Throwable e) {
                System.out.printf("async onException Throwable=%s %n", e);
            }

        });
    }
//    @PostMapping("/sendMessage9")
    public void sendMessage9(@RequestBody MessageTemplate messageTemplate){
        // 发送顺序排序消息
        rocketMQTemplate.syncSendOrderly("java1234-rocketmq", MessageBuilder.withPayload("Hello, World").build(), "hashkey");
    }

    @PostMapping("/sendMessageToRocketMQ")
    public void sendMessageToRocketMQ(@RequestBody MessageTemplate messageTemplate){

        String serverIp = messageTemplate.getServerIp();
        int messageTemplateId = messageTemplate.getId();
        String masterTableUniqueKey = messageTemplate.getMasterTableUniqueKey();
        String slaveTableUniqueKey = messageTemplate.getSlaveTableUniqueKey();
        String userCode = messageTemplate.getUserCode();
        //
        JSONObject jsonObject = new JSONObject();
        // 预防重复消费消息，方法1：生产消息时使用uuid，使用一张表或uuid放到message表，查看uuid是否已存在，增加了查询时间
        // 出现的概率比较低，重复了也影响不大，是否考虑允许重复消费
        // 是否有唯一键codeId
//        jsonObject.put("uuid", UUID.randomUUID());
        jsonObject.put("serverIp", serverIp);
        jsonObject.put("messageTemplateId", messageTemplateId);
        jsonObject.put("masterTableUniqueKey", masterTableUniqueKey);
        jsonObject.put("slaveTableUniqueKey", slaveTableUniqueKey);
        jsonObject.put("userCode", userCode);
//        rocketMQTemplate.convertAndSend("java1234-rocketmq", jsonObject.toString());
        rocketMQTemplate.syncSend("java1234-rocketmq", jsonObject.toString(),30000);

    }
}