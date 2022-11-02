package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.util.WxUtil;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.util.Arrays;

@RestController
@RequestMapping("/wx")
public class WxOfficialAccountController {

    public static final String token = "projectManagerSystem";

    /**
     * 验证消息的确来自微信服务器
     * @param request
     * @param response
     */
//    @GetMapping("/validate")
//    public void validateMessageFromWxServer(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
//        System.out.println("进来了wx/validate");
//        String signature = request.getParameter("signature");
//        String timestamp = request.getParameter("timestamp");
//        String nonce = request.getParameter("nonce");
//        String echostr = request.getParameter("echostr");
//        if(signature == null || timestamp == null || nonce == null || echostr == null) {
//            System.out.println("请求参数有误");
//            return;
//        }
////        1）将token、timestamp、nonce三个参数进行字典序排序
//        String[] strArray = {token, timestamp, nonce};
//        Arrays.sort(strArray);
////        2）将三个参数字符串拼接成一个字符串进行sha1加密
//        StringBuilder stringBuilder = new StringBuilder();
//        for(String str : strArray) {
//            stringBuilder.append(str);
//        }
//        String encryption = getSha1(stringBuilder.toString());
////        3）开发者获得加密后的字符串可与 signature 对比，标识该请求来源于微信
//        if(encryption.equals(signature)) {
//            out.print(echostr);
//            out.flush();
//            out.close();
//        }
//    }


    @PostMapping("/validate")
    public void handlerWechatEvent(HttpServletRequest request, HttpServletResponse response, @RequestBody String xmlData) {
        if(xmlData != null) {
            System.out.println("xmlData:" + xmlData);
        } else {
            System.out.println("XML 数据包为null");
        }

    }

    public static String getSha1(String str) {

        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes("UTF-8"));
            byte[] md = mdTemp.digest();
            int j = md.length;
            char buf[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf);
        } catch (Exception e) {
            return null;
        }
    }

    public String getWxOfficialAccountAccessToken() {
        String appid = "wx226ffc0b68fa17e9";
        String appsecret = "4a4037b79e0390da5a4d8cb8ff5014f0";
//		有效期两小时
        String accessToken = null;
        String expiresIn = null;
        String getTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
        getTokenUrl = String.format(getTokenUrl, appid, appsecret);
        JSONObject jsonObject = WxUtil.getDataFromWxServer(getTokenUrl);
        String errcode = jsonObject.getString("errcode");
        if(errcode == null) {
            System.out.println("getToken请求微信服务器成功");
            accessToken = jsonObject.getString("access_token");
            expiresIn = jsonObject.getString("expires_in");
            System.out.println("accessToken:" + accessToken);
            System.out.println("expiresIn:" + expiresIn);
            return accessToken;
        } else {
            String errmsg = jsonObject.getString("errmsg");
            System.out.println("getToken请求微信服务器失败，errcode:" + errcode + ",errmsg:" + errmsg);
            return null;
        }
    }
}
