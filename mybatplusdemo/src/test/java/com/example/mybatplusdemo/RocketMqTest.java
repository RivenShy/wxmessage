package com.example.mybatplusdemo;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RocketMqTest {

    private final static String nameServer = "127.0.0.1:9876";

    private final static String producerGroup = "my_group";

    private final static String topic = "topic-test";

    @Test
    public void syncSend() {
        try {
            // 初始化一个producer并设置Producer group name
            DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
            // 设置NameServer地址
            producer.setNamesrvAddr(nameServer);
            // 启动producer
            producer.start();
            // 创建一条消息，并指定topic、tag、body等信息，tag可以理解成标签，对消息进行再归类，RocketMQ可以在消费端对tag进行过滤
            Message msg = new Message(topic, "tagA", "Hello RocketMQ".getBytes(RemotingHelper.DEFAULT_CHARSET));
            // 利用producer进行发送，并同步等待发送结果
            SendResult sendResult = producer.send(msg, 10000);
            System.out.printf("%s%n", sendResult);
            // 一旦producer不再使用，关闭producer
            producer.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
