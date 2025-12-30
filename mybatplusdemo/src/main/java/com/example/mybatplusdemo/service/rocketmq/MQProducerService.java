package com.example.mybatplusdemo.service.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * mq 生产者
 *
 * @author zibo
 * @date 2023/5/17 15:48
 * @slogan 慢慢学，不要停。
 */
@Slf4j
@Service
public class MQProducerService {

    // 直接注入使用，用于发送消息到 broker 服务器
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 发送普通消息
     *
     * @param msg 消息可以是任何对象，如：String、Map、对象等
     */
    public void send(String msg) {
        // 写法一
        rocketMQTemplate.convertAndSend("springboot_topic:test", msg);
        // 写法二
        // rocketMQTemplate.send("springboot_topic:test", MessageBuilder.withPayload(msg).build());
    }

    /**
     * 发送单向消息
     *
     * @param msg 消息可以是任何对象，如：String、Map、对象等
     */
    public void sendOneWay(String msg) {
        rocketMQTemplate.sendOneWay("springboot_topic:test", msg);
    }

    /**
     * 发送同步消息
     *
     * @param msg 消息可以是任何对象，如：String、Map、对象等
     */
    public SendResult sendSync(String msg) {
        SendResult result = rocketMQTemplate.syncSend("springboot_topic:test", msg);
        log.info("发送结果：{}", result);
        return result;
    }

    /**
     * 发送异步消息
     *
     * @param msg 消息可以是任何对象，如：String、Map、对象等
     */
    public void sendAsync(String msg) {
        rocketMQTemplate.asyncSend("springboot_topic:test", msg, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("发送成功");
            }

            @Override
            public void onException(Throwable throwable) {
                log.info("发送失败");
            }
        });
    }

    /**
     * 发送延时消息
     *
     * @param msg 消息可以是任何对象，如：String、Map、对象等
     */
    public void sendDelay(String msg) {
        rocketMQTemplate.syncSendDelayTimeSeconds("springboot_topic:test", msg, 5);
    }

    /**
     * 发送顺序消息
     *
     * @param msg 消息可以是任何对象，如：String、Map、对象等
     */
    public void sendOrderly(String msg) {
        // 第一条
        rocketMQTemplate.syncSendOrderly("springboot_topic:test", msg, "1");
        // 第二条
        rocketMQTemplate.syncSendOrderly("springboot_topic:test", msg, "2");
        // 第三条
        rocketMQTemplate.syncSendOrderly("springboot_topic:test", msg, "3");
    }

    /**
     * 发送批量消息
     *
     * @param msgList 消息列表
     */
    public void sendBatch(List<String> msgList) {
        List<Message<String>> rocketMQMessages = new ArrayList<>();
        for (String msg : msgList) {
            rocketMQMessages.add(MessageBuilder.withPayload(msg).build());
        }
        rocketMQTemplate.syncSend("springboot_topic:test", rocketMQMessages);
    }
}