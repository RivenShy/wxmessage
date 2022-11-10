package com.example.demo.util;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.controller.WxOfficialAccountController;
import com.example.demo.entity.UserInfo;
import com.example.demo.entity.WxAccessToken;
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
import org.apache.log4j.Logger;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class WxUtil {

    private static Logger logger = Logger.getLogger(WxUtil.class);

    public static ConcurrentHashMap<String, WxAccessToken> mapAccessToken = new ConcurrentHashMap<>();
    public static final String mapAccessTokenKey = "wxAccessToken";

    // 模拟已登录用户
    public static ConcurrentHashMap<String, UserInfo> session = new ConcurrentHashMap<>();


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

    public static String getAccessToken() {
        WxAccessToken wxAccessTokenCache = mapAccessToken.get(mapAccessTokenKey);
        if(wxAccessTokenCache != null) {
            Date now = new Date();
            if(now.before(wxAccessTokenCache.getExpireDate())){
                System.out.println("缓存获取accessToken");
                return wxAccessTokenCache.getAccessToken();
            }
        }
        String appid = WxOfficialAccountController.appid;
        String appsecret = WxOfficialAccountController.appsecret;
//		有效期两小时
        String accessToken = null;
        int expiresIn = 0;
        String getTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
        getTokenUrl = String.format(getTokenUrl, appid, appsecret);
        JSONObject jsonObject = WxUtil.getDataFromWxServer(getTokenUrl);
        String errcode = jsonObject.getString("errcode");
        if(errcode == null) {
            System.out.println("getToken请求微信服务器成功");
            accessToken = jsonObject.getString("access_token");
            expiresIn = jsonObject.getInteger("expires_in");
            System.out.println("accessToken:" + accessToken);
            System.out.println("expiresIn:" + expiresIn);
        } else {
            String errmsg = jsonObject.getString("errmsg");
            System.out.println("getToken请求微信服务器失败，errcode:" + errcode + ",errmsg:" + errmsg);
        }
        WxAccessToken wxAccessToken = new WxAccessToken();
        wxAccessToken.setAccessToken(accessToken);
        wxAccessToken.setExpireDate(getDateInSecond(new Date(), expiresIn));
        mapAccessToken.put(mapAccessTokenKey, wxAccessToken);
        return accessToken;
    }

    public static Date getDateInSecond(Date date, int second) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, second);
        return cal.getTime();
    }

    public static void sendOuthMsgToUser(String openid, String outhUrl) {
//        String openid = "oa9m45sEj_dDR9FGoeoGmx7_M21o";
        String template_id = "McTEif4kxJ-BNiLp1fMFYR8Ymzc5kJoujeIq1dhqL20";
        String REDIRECT_URI = "http://www.szwd.online//wx/outhPage/" + 1;
//        String outhUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
        outhUrl = String.format(outhUrl, WxOfficialAccountController.appid, REDIRECT_URI);
        String url = outhUrl;
        //
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("touser", openid);
        jsonObject.put("template_id", template_id);
        jsonObject.put("url", url);
        JSONObject jsonObject2 = new JSONObject();
        //
        JSONObject jsonObjectFirst = new JSONObject();
        jsonObjectFirst.put("value", "您好，由于您申请了工程项目管理系统绑定申请，现已生成授权申请。");
        jsonObjectFirst.put("color", "#173177");
        jsonObject2.put("first", jsonObjectFirst);
        //
        JSONObject jsonObjectKeyword1 = new JSONObject();
        jsonObjectKeyword1.put("value", "工程项目管理系统");
        jsonObjectKeyword1.put("color", "#173177");
        jsonObject2.put("keyword1", jsonObjectKeyword1);
        //
        JSONObject jsonObjectKeyword2 = new JSONObject();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jsonObjectKeyword2.put("value", simpleDateFormat.format(new Date()));
        jsonObjectKeyword2.put("color", "#173177");
        jsonObject2.put("keyword2", jsonObjectKeyword2);
        //
        JSONObject jsonObjectRemark = new JSONObject();
        jsonObjectRemark.put("value", "请点击详情进行授权操作");
        jsonObjectRemark.put("color", "#173177");
        jsonObject2.put("remark", jsonObjectRemark);
        //
        jsonObject.put("data", jsonObject2);
        //
//		String accessToken = WxUtil.getAccessToken();
        String accessToken = WxUtil.getAccessToken();
        String sendMsgUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s";
        sendMsgUrl = String.format(sendMsgUrl, accessToken);
        System.out.println("sendMsgUrl:" + sendMsgUrl);
        System.out.println("jsonObject:" + jsonObject.toString());
        JSONObject wxResult = WxUtil.postToWxServer(sendMsgUrl, jsonObject.toString());
        String errcode = wxResult.getString("errcode");
        String errmsg = wxResult.getString("errmsg");
        String msgid = wxResult.getString("msgid");
        System.out.println("errcode:" + errcode + ", errmsg:" + errmsg + ", msgid:" + msgid);
    }

    public static boolean sendProcessApprovalMsgToUser(String openid, int messageId) {
        String template_id = Constant.processApproveRemindTemplateId;
        String url = "http://www.szwd.online/userInfo/userClickWxMessage/" + messageId;
        //
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("touser", openid);
        jsonObject.put("template_id", template_id);
        jsonObject.put("url", url);
        JSONObject jsonObject2 = new JSONObject();
        //
        JSONObject jsonObjectFirst = new JSONObject();
        jsonObjectFirst.put("value", "微信绑定公众号提醒");
        jsonObjectFirst.put("color", "#173177");
        jsonObject2.put("first", jsonObjectFirst);
        //
        JSONObject jsonObjectKeyword1 = new JSONObject();
        jsonObjectKeyword1.put("value", "JP1409010001");
        jsonObjectKeyword1.put("color", "#173177");
        jsonObject2.put("keyword1", jsonObjectKeyword1);
        //
        JSONObject jsonObjectKeyword2 = new JSONObject();
        jsonObjectKeyword2.put("value", "2014-09-01");
        jsonObjectKeyword2.put("color", "#173177");
        jsonObject2.put("keyword2", jsonObjectKeyword2);
        //
        JSONObject jsonObjectKeyword3 = new JSONObject();
        jsonObjectKeyword3.put("value", "张三");
        jsonObjectKeyword3.put("color", "#173177");
        jsonObject2.put("keyword3", jsonObjectKeyword3);
        //
        JSONObject jsonObjectKeyword4 = new JSONObject();
        jsonObjectKeyword4.put("value", "财务部");
        jsonObjectKeyword4.put("color", "#173177");
        jsonObject2.put("keyword4", jsonObjectKeyword4);
        //
        JSONObject jsonObjectKeyword5 = new JSONObject();
        jsonObjectKeyword5.put("value", "申请一部笔记本电脑");
        jsonObjectKeyword5.put("color", "#173177");
        jsonObject2.put("keyword5", jsonObjectKeyword5);
        //
        JSONObject jsonObjectRemark = new JSONObject();
        jsonObjectRemark.put("value", "请点击查看详情");
        jsonObjectRemark.put("color", "#173177");
        jsonObject2.put("remark", jsonObjectRemark);
        //
        jsonObject.put("data", jsonObject2);
        //
        String accessToken = WxUtil.getAccessToken();
        String sendMsgUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s";
        sendMsgUrl = String.format(sendMsgUrl, accessToken);
        logger.info("sendMsgUrl:" + sendMsgUrl);
        logger.info("jsonObject:" + jsonObject.toString());
        JSONObject wxResult = WxUtil.postToWxServer(sendMsgUrl, jsonObject.toString());
        String errcode = wxResult.getString("errcode");
        String errmsg = wxResult.getString("errmsg");
        String msgid = wxResult.getString("msgid");
        logger.info("errcode:" + errcode + ", errmsg:" + errmsg + ", msgid:" + msgid);
        if(errcode.equals("0")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean sendRemoteLoginMsg(String openid, int messageId) {
        String template_id = Constant.remoteLoginRemindTemplateId;
        String url = "http://www.szwd.online/userInfo/userClickWxMessage/" + messageId;
        //
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("touser", openid);
        jsonObject.put("template_id", template_id);
        jsonObject.put("url", url);
        JSONObject jsonObject2 = new JSONObject();
        //
        JSONObject jsonObjectFirst = new JSONObject();
        jsonObjectFirst.put("value", "您好，您的账号在异地登录");
        jsonObjectFirst.put("color", "#173177");
        jsonObject2.put("first", jsonObjectFirst);
        //
        JSONObject jsonObjectKeyword1 = new JSONObject();
        jsonObjectKeyword1.put("value", "xxxxxx");
        jsonObjectKeyword1.put("color", "#173177");
        jsonObject2.put("keyword1", jsonObjectKeyword1);
        //
        JSONObject jsonObjectKeyword2 = new JSONObject();
        jsonObjectKeyword2.put("value", new Date());
        jsonObjectKeyword2.put("color", "#173177");
        jsonObject2.put("keyword2", jsonObjectKeyword2);
        //
        JSONObject jsonObjectKeyword3 = new JSONObject();
        jsonObjectKeyword3.put("value", "xxxxxx");
        jsonObjectKeyword3.put("color", "#173177");
        jsonObject2.put("keyword3", jsonObjectKeyword3);
        //
        JSONObject jsonObjectKeyword4 = new JSONObject();
        jsonObjectKeyword4.put("value", "xxxxxx");
        jsonObjectKeyword4.put("color", "#173177");
        jsonObject2.put("keyword4", jsonObjectKeyword4);
        //
        JSONObject jsonObjectKeyword5 = new JSONObject();
        jsonObjectKeyword5.put("value", "申请一部笔记本电脑");
        jsonObjectKeyword5.put("color", "#173177");
        jsonObject2.put("keyword5", jsonObjectKeyword5);
        //
        JSONObject jsonObjectRemark = new JSONObject();
        jsonObjectRemark.put("value", "如非本人操作，请立即修改密码！");
        jsonObjectRemark.put("color", "#173177");
        jsonObject2.put("remark", jsonObjectRemark);
        //
        jsonObject.put("data", jsonObject2);
        //
        String accessToken = WxUtil.getAccessToken();
        String sendMsgUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s";
        sendMsgUrl = String.format(sendMsgUrl, accessToken);
        logger.info("sendMsgUrl:" + sendMsgUrl);
        logger.info("jsonObject:" + jsonObject.toString());
        JSONObject wxResult = WxUtil.postToWxServer(sendMsgUrl, jsonObject.toString());
        String errcode = wxResult.getString("errcode");
        String errmsg = wxResult.getString("errmsg");
        String msgid = wxResult.getString("msgid");
        logger.info("errcode:" + errcode + ", errmsg:" + errmsg + ", msgid:" + msgid);
        if(errcode.equals("0")) {
            return true;
        } else {
            return false;
        }
    }
}
