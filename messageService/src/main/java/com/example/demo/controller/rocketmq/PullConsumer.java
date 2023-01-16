//package com.example.demo.controller.rocketmq;
//
//import org.apache.rocketmq.common.message.MessageExt;
//import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
//import org.apache.rocketmq.spring.core.RocketMQListener;
//import org.apache.rocketmq.spring.core.RocketMQTemplate;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import javax.annotation.Resource;
//import java.util.List;
//
///**
// * pull模式消费消息
// */
////@RocketMQMessageListener(topic = "java1234-rocketmq",consumerGroup ="${rocketmq.consumer.group}" )
////@Component
////@RequestMapping("/pullConsumer")
//public class PullConsumer {
//
//    @Resource
//    private RocketMQTemplate rocketMQTemplate;
//
//    @RequestMapping("/consume")
//    public void pullMessage() {
//        //This is an example of pull consumer using rocketMQTemplate.
//        List<String> messages = rocketMQTemplate.receive(String.class);
//        System.out.printf("receive from rocketMQTemplate, messages=%s %n", messages);
//    }
//
//    @GetMapping("/test2")
//    public String fun2() {
//        List<MessageExt> receive = rocketMQTemplate.receive(MessageExt.class);
//        for (MessageExt item : receive){
//            System.out.println(item);
//        }
//        return "成功";
//    }
//}