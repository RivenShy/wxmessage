package com.example.mybatplusdemo.config;

public class WechatUrlConfig {
    /**
     * 适用对象：小程序下单
     * 请求方式：POST
     */
    public static final String JSAPIURL = "https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi";

    /**
     * 适用对象：APP下单
     * 请求方式：POST
     */
    public static final String APPURL = "https://api.mch.weixin.qq.com/v3/pay/transactions/app";

    /**
     * 获取证书
     */
    public static final String CERTIFICATESURL = "https://api.mch.weixin.qq.com/v3/certificates";


    /**
     * 退款地址
     */
    public static final String REFUNDSURL = "https://api.mch.weixin.qq.com/v3/refund/domestic/refunds";
}