package com.example.mybatplusdemo.controller;

import com.example.mybatplusdemo.config.WechatUrlConfig;
import com.example.mybatplusdemo.config.WxpayConfig;
import com.example.mybatplusdemo.entity.Product;
import com.example.mybatplusdemo.utils.HttpUtils;
import com.example.mybatplusdemo.utils.WeixinchatPayUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信支付相关接口
 *
 * @author xsk
 */
@RestController
@RequestMapping("/wx/pay")
@Slf4j
public class WxPayController {


    public Map<String, Object> wxPay(String openid, Integer type, Product product) throws JsonProcessingException, JsonProcessingException {
        Map<String, Object> map = new HashMap();
        // 支付的产品（小程序或者公众号，主要需要和微信支付绑定哦）
        map.put("appid", WxpayConfig.app_id);
        // 支付的商户号
        map.put("mchid", WxpayConfig.mch_id);
        //临时写死配置
        map.put("description", product.getSubject());
        map.put("out_trade_no", product.getOutTradeNo());
        map.put("notify_url", WxpayConfig.notify_order_url);

        Map<String, Object> amount = new HashMap();
        //订单金额 单位分
        amount.put("total", Integer.parseInt(product.getTotalFee()) * 100);
        amount.put("currency", "CNY");
        map.put("amount", amount);
        // 设置小程序所需的opendi
        Map<String, Object> payermap = new HashMap();
        payermap.put("openid", openid);
        map.put("payer", payermap);

        ObjectMapper objectMapper = new ObjectMapper();
        String body = objectMapper.writeValueAsString(map);

        Map<String, Object> stringObjectMap = null;
        HashMap<String, Object> dataMap = null;
        try {
            switch (type) {
                case 1:
                    stringObjectMap = HttpUtils.doPostWexin(WechatUrlConfig.JSAPIURL, body);
                    dataMap = WeixinchatPayUtils.getTokenJSAPI(WxpayConfig.app_id, String.valueOf(stringObjectMap.get("prepay_id")));
                    break;
                default:
                    stringObjectMap = HttpUtils.doPostWexin(WechatUrlConfig.APPURL, body);
                    dataMap = WeixinchatPayUtils.getTokenApp(WxpayConfig.app_id, String.valueOf(stringObjectMap.get("prepay_id")));
                    break;
            }
            return dataMap;
        } catch (Exception ex) {
        }
        return null;
    }

}
