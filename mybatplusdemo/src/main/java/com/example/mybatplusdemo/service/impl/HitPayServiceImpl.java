package com.example.mybatplusdemo.service.impl;

import com.example.mybatplusdemo.model.User;
import com.example.mybatplusdemo.service.HitPayService;
import com.example.mybatplusdemo.utils.WechatPayUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.formula.functions.T;
import org.eclipse.parsson.JsonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.awt.print.Book;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class HitPayServiceImpl implements HitPayService {
    @Value("${hitpay.merchant-id}")
    private String merchantId;
    @Value("${hitpay.api-key}")
    private String apiKey;
    @Value("${hitpay.api-base-url}")
    private String apiBaseUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${hitpay.endpoint}")
    private String endpoint;

    @Resource
    private OkHttpClient okHttpClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final ObjectMapper JSON=new ObjectMapper();

//    @Override
//    public String initiatePayment(String paymentDetails) throws IOException {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("Merchant-ID", merchantId);
//        headers.set("Authorization", "Bearer " + apiKey);
//        HttpEntity<String> entity = new HttpEntity<>(paymentDetails, headers);
////        String paymentInitiationUrl = apiBaseUrl + "payments/initiate";
//        String paymentInitiationUrl = apiBaseUrl + "payments/initiate";
//        return restTemplate.postForObject(paymentInitiationUrl, entity, String.class);
//    }

//    @Override
//    public String initiatePayment(String paymentDetails) throws IOException {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
////        headers.set("Merchant-ID", merchantId);
////        headers.set("Authorization", "Bearer " + apiKey);
//        HttpEntity<String> entity = new HttpEntity<>(paymentDetails, headers);
////        String paymentInitiationUrl = apiBaseUrl + "payments/initiate";
//        String paymentInitiationUrl = apiBaseUrl + "payment-requests";
//        Map<String, Object> requestParams = new HashMap<>();
//        requestParams.put("amount", "1");
//        requestParams.put("currency", "SGD");
//        requestParams.put("reference_number", "REF123");
//        requestParams.put("redirect_url", "https://yourdomain.com/payment-success");
//        requestParams.put("webhook", "https://yourdomain.com/webhook");
//        return restTemplate.postForObject(paymentInitiationUrl, requestParams, String.class);
//    }

//    public String initiatePayment(String a) {
//        String url = "https://api.sandbox.hit-pay.com/v1/payment-requests";
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        headers.set("X-BUSINESS-API-KEY", "a5218db6c252258c9b3f4989b52d898b05a070b01ad812abbcfcdf0ee354014c");
//        headers.set("X-Requested-With", "XMLHttpRequest");
//
//        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
//        requestBody.add("email", "tom@test.com");
//        requestBody.add("redirect_url", "https://test.com/success");
//        requestBody.add("reference_number", "REF123");
//        requestBody.add("webhook", "https://test.com/webhook");
//        requestBody.add("currency", "SGD");
//        requestBody.add("amount", "599");
//
//        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(requestBody, headers);
//
//        ResponseEntity<String> responseEntity = restTemplate.exchange(
//                url,
//                HttpMethod.POST,
//                entity,
//                String.class
//        );
//
//        return responseEntity.getBody();
//    }

    public  String initiatePayment(String a) throws JsonProcessingException {
        CloseableHttpClient httpClient =  HttpClients.createDefault();
        HttpPost httpPost  = new HttpPost("https://api.sandbox.hit-pay.com/v1/payment-requests");

        Map<String, Object> map = new HashMap();
        map.put("currency", "SGD");
        map.put("amount", "599");
        String body = objectMapper.writeValueAsString(map);
        httpPost.addHeader("Content-Type","application/json;chartset=utf-8");
        httpPost.addHeader("Accept", "application/json");
        //        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpPost.addHeader("X-BUSINESS-API-KEY", "a5218db6c252258c9b3f4989b52d898b05a070b01ad812abbcfcdf0ee354014c");
//        headers.set("X-Requested-With", "XMLHttpRequest");
//
        try{
//            String token = WechatPayUtils.getToken("POST", new URL(url), body);
//            httpPost.addHeader("Authorization", token);

            if(body==null){
                throw  new IllegalArgumentException("data参数不能为空");
            }
            StringEntity stringEntity = new StringEntity(body,"utf-8");
            httpPost.setEntity(stringEntity);

            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            org.apache.http.HttpEntity httpEntity = httpResponse.getEntity();

            if(httpResponse.getStatusLine().getStatusCode() == 200){
                String jsonResult = EntityUtils.toString(httpEntity);
                return JSON.readValue(jsonResult, HashMap.class).toString();
            }else{
                System.err.println("微信支付错误信息"+EntityUtils.toString(httpEntity));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                httpClient.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }


//    public Map<String, Object> createPayment(double amount, String currency, String reference) {
//        try {
//            // HitPay 请求 URL
//            String url = endpoint + "/payment-requests";
//
//            // 请求参数
//            Map<String, Object> requestParams = new HashMap<>();
//            requestParams.put("amount", amount);
//            requestParams.put("currency", currency);
//            requestParams.put("reference_number", reference);
//            requestParams.put("redirect_url", "https://yourdomain.com/payment-success");
//            requestParams.put("webhook", "https://yourdomain.com/webhook");
//
//            // 添加 API 密钥
//            requestParams.put("api_key", apiKey);
//
//
//            // 发送 POST 请求
//            String response = restTemplate.postForObject(url, requestParams, String.class);
//
//            // 解析响应
//            return objectMapper.readValue(response, Map.class);
//
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to create payment", e);
//        }
//    }

//    public Map<String, Object> createPayment(double amount, String currency, String reference) {
//        try {
//            // HitPay 请求 URL
//            String url = endpoint + "/payment-requests?amount=1&currency=SGD";
//
//            // 请求参数
//            Map<String, Object> requestParams = new HashMap<>();
//            requestParams.put("amount", amount);
//            requestParams.put("currency", currency);
//            requestParams.put("reference_number", reference);
//            requestParams.put("redirect_url", "https://yourdomain.com/payment-success");
//            requestParams.put("webhook", "https://yourdomain.com/webhook");
//
//            // 添加 API 密钥
//            requestParams.put("X-BUSINESS-API-KEY", apiKey);
//            requestParams.put("api_key", apiKey);
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//            headers.add("X-BUSINESS-API-KEY", apiKey);
//            headers.add("X-Requested-With", "XMLHttpRequest");
//            headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
//            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(requestParams.toString(), headers);
//            // 发送 POST 请求
//            String response = restTemplate.postForObject(url, entity, String.class);
//
//            // 解析响应
//            return objectMapper.readValue(response, Map.class);
//
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to create payment", e);
//        }
//    }

     /*   * 创建支付请求
     * @param amount 支付金额
     * @param currency 货币类型（例如：SGD, USD）
            * @param reference 订单号或参考编号
     * @return 支付请求响应
     */
//    public Map<String, Object> createPayment(double amount, String currency, String reference) {
//        try {
//            // 沙箱 API 请求 URL
//            String url = endpoint + "/payment-requests";
//
//            // 请求参数
//            Map<String, Object> requestParams = new HashMap<>();
//            requestParams.put("amount", amount);
//            requestParams.put("currency", currency);
//            requestParams.put("reference_number", reference);
//            requestParams.put("redirect_url", "https://yourdomain.com/payment-success");  // 支付成功后的回调 URL
//            requestParams.put("webhook", "https://yourdomain.com/webhook");  // 支付通知的 Webhook URL
//            requestParams.put("api_key", apiKey);  // API 密钥
//
//            // 发送 POST 请求
//            String response = restTemplate.postForObject(url, requestParams, String.class);
//
//            // 解析响应
//            return objectMapper.readValue(response, Map.class);
//
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to create payment", e);
//        }
//    }




    public Map<String, Object> createPayment(double amount, String currency, String reference) {
        try {
            // 沙箱 API 请求 URL
            String url = "https://api.sandbox.hit-pay.com/v1/payment-requests";
            Map<String, Object> paymentData = new HashMap<>();
            paymentData.put("amount", "100.0");
//            paymentData.put("currency", "SGD");
            paymentData.put("currency", "USD");
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.addHeader("X-BUSINESS-API-KEY", "a5218db6c252258c9b3f4989b52d898b05a070b01ad812abbcfcdf0ee354014c");
            // 假设jsonBody是前面构建好的JSON格式请求体
            StringEntity stringEntity = new StringEntity(objectMapper.writeValueAsString(paymentData));
            httpPost.setEntity(stringEntity);
            CloseableHttpResponse httpResponse = null;
            httpResponse = httpClient.execute(httpPost);
            HttpEntity responseEntity =  httpResponse.getEntity();
            String responseString = EntityUtils.toString(responseEntity);
            System.out.println(responseString);
            Map<String, Object> responseMap = objectMapper.readValue(responseString, Map.class);
//            System.out.println(responseMap);
            String responseUrl = (String) responseMap.get("url");
            System.out.println(responseUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


}