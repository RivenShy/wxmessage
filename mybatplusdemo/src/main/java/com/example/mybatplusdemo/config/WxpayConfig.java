package com.example.mybatplusdemo.config;


import lombok.Data;
import org.springframework.stereotype.Component;

import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Component
public class WxpayConfig {
    public static String app_id = "*****************"; // 公众账号ID

    public static String mch_id = "******************************"; // 商户号

    public static String mchSerialNo = "********************************************"; //微信商家api序列号
    public static String v3Key = "*********"; // 回调报文解密V3密钥key

    public static String KeyPath = "*************************************"; // 商户的key【API密匙】存放路径


    public static String notify_order_url = "*************************************"; // 服务器异步通知页面路径--下单

    public static String notify_refound_url = "*************************************"; // 服务器异步通知页面路径-退款


    public static String return_url = "*************************************"; // 服务器同步通知页面路径


    public static Map<String, X509Certificate> certificateMap = new ConcurrentHashMap<>(); // 定义全局容器 保存微信平台证书公钥
}