package com.example.mybatplusdemo;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.example.mybatplusdemo.config.websocket.WebSocketConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class ChatGptTest {

    @Test
    public void test1() {
        String content = "该不该吃早餐？";
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        json.set("model", "gpt-3.5-turbo");
        JSONObject msg = new JSONObject();
        msg.set("role", "user");
        msg.set("content",content);
        JSONArray array = new JSONArray();
        array.add(msg);
        json.set("messages", array);
        json.set("temperature", 0);
        json.set("max_tokens", 2048);
        json.set("top_p", 1);
        json.set("frequency_penalty", 0.0);
        json.set("presence_penalty", 0.0);
        try {
//            Proxy proxy = Proxys.http("地址", 端口号);
            HttpResponse response = HttpRequest.post("https://api.openai.com/v1/chat/completions")
                    .headerMap(headers, false)
                    .bearerAuth("apikey")
//                    .setProxy(proxy)
                    .body(String.valueOf(json))
                    .timeout(600000)
                    .execute();
            System.out.println(response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}