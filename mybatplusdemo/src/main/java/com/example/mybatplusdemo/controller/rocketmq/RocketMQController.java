package com.example.mybatplusdemo.controller.rocketmq;

import com.example.mybatplusdemo.service.rocketmq.MQProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * mq 接口
 *
 * @author zibo
 * @date 2023/5/17 15:48
 * @slogan 慢慢学，不要停。
 */
@RestController
@RequestMapping("/rocketmq")
public class RocketMQController {

    @Autowired
    private MQProducerService mqProducerService;

    @GetMapping("/send")
    public void send() {
        mqProducerService.send("测试消息");

        mqProducerService.sendOneWay("单向消息");

        mqProducerService.sendSync("同步消息");

        mqProducerService.sendAsync("异步消息");

        mqProducerService.sendDelay("延时消息");

        mqProducerService.sendOrderly("顺序消息");

        List<String> msgList = new ArrayList<>();
        for(int i=0; i<10; i++) {
            msgList.add("批量消息" + i);
        }
        mqProducerService.sendBatch(msgList);
    }

}