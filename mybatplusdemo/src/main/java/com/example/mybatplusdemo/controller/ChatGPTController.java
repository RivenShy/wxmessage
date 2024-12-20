package com.example.mybatplusdemo.controller;

import cn.hutool.http.*;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.plexpt.chatgpt.util.Proxys;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chatGpt")
public class ChatGPTController {


    public static final String MODEL_3 = "gpt-3.5-turbo";
    public static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String OPENAI_KEY = "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";


    @GetMapping("/ask")
    public static void chat(@RequestParam("content")String content) {
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
//            String url = "https://api.openai.com/v1/chat/completions";
            String url = "https://api.openai.com/v1/completions";

            HttpResponse response = HttpRequest.post(url)
                    .headerMap(headers, false)
                    .bearerAuth("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")
//                    .setProxy(proxy)
                    .body(String.valueOf(json))
                    .timeout(600000)
                    .execute();
            System.out.println(response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @GetMapping("/chat")
    public String chat2(@RequestParam("prompt") String prompt) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            final HttpHeaders headers = request.getHeaders();
            headers.add("Authorization", "Bearer " + OPENAI_KEY);
            headers.add("Content-Type", "application/json");
            return execution.execute(request, body);
        });
        List<Map<String, Object>> messages = new ArrayList<>();

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        messages.add(message);

        Map<String, Object> param = new HashMap<>();
        param.put("model", MODEL_3);
        param.put("messages", messages);
        param.put("temperature", 0.7);

        ChatResponse response = restTemplate.postForObject(API_URL, param, ChatResponse.class);
        return response.getChoices().get(0).getMessage().getContent();
    }
}


@Data
@AllArgsConstructor
@NoArgsConstructor
class ChatResponse {

    private List<Choice> choices;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Choice {

        private int index;
        private Message message;
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Message {
    private String role;
    private String content; //prompt
}
