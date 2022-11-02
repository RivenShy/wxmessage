package com.example.demo.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class WxUtil {

    public static JSONObject getDataFromWxServer(String url) {
        HttpClient httpClient = HttpClientBuilder.create().build();

        try {
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpGet);// 接收client执行的结果
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String result = EntityUtils.toString(entity, "UTF-8");
                return JSONObject.parseObject(result);
            } else {
                System.out.println("向微信服务器发送Get请求发生错误！");
                return null;
            }
        } catch (Exception e) {
            System.out.println("向微信服务器发送Get请求发生错误：" + e.getMessage());
            return null;
        }
    }

    public static JSONObject postToWxServer(String url, String params) {// ...
        HttpClient httpClient = HttpClientBuilder.create().build();
        // 创建POST请求
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-Type", "application/json");

        StringEntity entity = new StringEntity(params, "UTF-8");
        httpPost.setEntity(entity);

        try {
            HttpResponse response = httpClient.execute(httpPost);
            StatusLine status = response.getStatusLine();
            int state = status.getStatusCode();
            System.out.println("请求返回:" + state + "(" + url + ")");

            if (state == HttpStatus.SC_OK) {
                HttpEntity reEntity = response.getEntity();
                String jsonString = EntityUtils.toString(reEntity);
                System.out.println("jsonString:" + jsonString);
                return JSONObject.parseObject(jsonString);
            }
        } catch (Exception ex) {
            System.out.println("向微信服务器发送Post请求出错：" + ex.getMessage());
        }
        return null;
    }
}
