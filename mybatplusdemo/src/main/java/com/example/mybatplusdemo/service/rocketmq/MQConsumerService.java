package com.example.mybatplusdemo.service.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

/**
 * mq 消费者
 *
 * @author zibo
 * @date 2023/5/17 15:48
 * @slogan 慢慢学，不要停。
 */
@Slf4j
//@Service
//@RocketMQMessageListener(topic = "springboot_topic", selectorExpression = "test", consumerGroup = "springboot_consumer_group")
public class MQConsumerService implements RocketMQListener<String> {

    // 监听到消息就会执行此方法
    @Override
    public void onMessage(String msg) {
        log.info("监听到消息：msg={}", msg);
    }
}