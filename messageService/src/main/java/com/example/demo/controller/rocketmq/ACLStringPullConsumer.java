//package com.example.demo.controller.rocketmq;
//
//import org.apache.rocketmq.spring.core.RocketMQTemplate;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.Resource;
//import java.util.List;
//
///**
// * RocketMQ Pull模式消费
// */
////@Service
//public class ACLStringPullConsumer implements CommandLineRunner {
//    public static volatile boolean running = true;
//    @Resource
//    private RocketMQTemplate rocketMQTemplate;
//
//    @Override
//    public void run(String... args) throws Exception {
//        // List messages = rocketMQTemplate.receive(String.class);
//        // System.out.printf(“receive from rocketMQTemplate, messages=%s %n”, messages);
//        while (running) {
//            List messages = rocketMQTemplate.receive(String.class);
//            if(messages.size()>0){
//                System.out.println("messages.size:" + messages.size());
//                for(int i=0;i<messages.size();i++){
//                    System.out.println("msgBody:" + messages.get(i));
//                }
//            }
//         Thread.sleep(1000); //1秒钟拉取一批数据,每批数据量由配置rocketmq.consumer.pull-batch-size决定
//        }
//    }
//}