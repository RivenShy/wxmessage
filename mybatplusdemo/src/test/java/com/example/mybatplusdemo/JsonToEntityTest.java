package com.example.mybatplusdemo;


import co.elastic.clients.json.JsonData;
import com.alibaba.fastjson.JSONObject;
import com.example.mybatplusdemo.entity.HttpResult;
import com.example.mybatplusdemo.entity.Menu;
import com.example.mybatplusdemo.entity.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.SimpleType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JsonToEntityTest {

    @Test
    public void testJsonToEntity() throws JsonProcessingException {

        Product product = mockCallHttpRequest(Product.class);
        System.out.println(product.toString());
        Menu menu = mockCallHttpRequest(Menu.class);
        System.out.println(menu.toString());
    }

    public <T> T mockCallHttpRequest(Class<T> clz) throws JsonProcessingException {
        return mockExecuteRequest(SimpleType.constructUnsafe(clz));
    }

    public <T> T mockExecuteRequest(JavaType javaType) throws JsonProcessingException {
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonData = new JSONObject();
//        jsonData.put("subject", "你好");
//        jsonData.put("productId", "1234");
        jsonData.put("name", "nameTest");
        jsonObject.put("data", jsonData);
        jsonObject.put("code", 200);
        jsonObject.put("message", "");
        jsonObject.put("ok", true);
        ObjectMapper objectMapper = new ObjectMapper();
//        JavaType javaType = SimpleType.constructUnsafe(Product.class);
        JavaType resultType = objectMapper.getTypeFactory().constructParametricType(HttpResult.class, javaType);
        HttpResult<T> httpResult = objectMapper.readValue(jsonObject.toJSONString(), resultType);
        return httpResult.getData();
    }
}
