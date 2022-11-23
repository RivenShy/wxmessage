package com.example.demo.util;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.controller.WxOfficialAccountController;
import com.example.demo.entity.AuditDelayCount;
import com.example.demo.entity.PendingApproval;
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

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
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

    public static void sendOuthMsgToUser(String openid, String outhUrl, String customerName, String userId, String userName) {
//        String openid = "oa9m45sEj_dDR9FGoeoGmx7_M21o";
        String template_id = "McTEif4kxJ-BNiLp1fMFYR8Ymzc5kJoujeIq1dhqL20";
//        String REDIRECT_URI = "http://www.szwd.online//wx/outhPage/" + 1;
//        String outhUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
//        outhUrl = String.format(outhUrl, WxOfficialAccountController.appid, REDIRECT_URI);
        String url = outhUrl;
        //
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("touser", openid);
        jsonObject.put("template_id", template_id);
        jsonObject.put("url", url);
        JSONObject jsonObject2 = new JSONObject();
        //
        JSONObject jsonObjectFirst = new JSONObject();
        jsonObjectFirst.put("value", "您好，由于您申请了以下单位的绑定申请，现已生成授权申请。");
        jsonObjectFirst.put("color", "#173177");
        jsonObject2.put("first", jsonObjectFirst);
        //
        String keyword1Content = userName == null ? userId : userName;
        keyword1Content = keyword1Content + "（" + customerName + "）";
        JSONObject jsonObjectKeyword1 = new JSONObject();
        jsonObjectKeyword1.put("value", keyword1Content);
        jsonObjectKeyword1.put("color", "#173177");
        jsonObject2.put("keyword1", jsonObjectKeyword1);
        //
        JSONObject jsonObjectKeyword2 = new JSONObject();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jsonObjectKeyword2.put("value", simpleDateFormat.format(new Date()));
        jsonObjectKeyword2.put("color", "#173177");
        jsonObject2.put("keyword2", jsonObjectKeyword2);
        //
        String remarkMessage = "点击这里授权";
        JSONObject jsonObjectRemark = new JSONObject();
        jsonObjectRemark.put("value", remarkMessage);
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

    public static boolean sendProcessApprovalMsgToUser(String customerName, UserInfo userInfo, int messageId, AuditDelayCount auditDelayCount) {
        String template_id = Constant.processApproveRemindTemplateId;
        // todo 改成一个前端页面（早上详情），传messgeId、userCode参数给这个页面
        String url = "http://www.szwd.online:8009/#/ApprovalInformation?messageId=" + messageId + "&userCode=" + userInfo.getUserId();
//        String url = "http://www.szwd.online/userInfo/userClickWxMessage/" + messageId;
//        String url = "http://www.szwd.online/userInfo/userClickWxMessage？messageId=" + messageId + "&userCode=" + userInfo.getUserId();
        //
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("touser", userInfo.getOpenId());
        jsonObject.put("template_id", template_id);
        jsonObject.put("url", url);
        JSONObject jsonObject2 = new JSONObject();
        //
        String userName = userInfo.getUserName() == null ? userInfo.getUserId() : userInfo.getUserName();
        JSONObject jsonObjectFirst = new JSONObject();
        jsonObjectFirst.put("value", "您好，" + userName + "，今天需要您审批的单据汇总信息如下");
        jsonObjectFirst.put("color", "#173177");
        jsonObject2.put("first", jsonObjectFirst);
        //
        String keyword1Content = "待审批单据总数：" + auditDelayCount.getAdcount() + "\n" + "已延期的单据数：" + auditDelayCount.getDelaycount();
        JSONObject jsonObjectKeyword1 = new JSONObject();
        jsonObjectKeyword1.put("value", keyword1Content);
        jsonObjectKeyword1.put("color", "#173177");
        jsonObject2.put("keyword1", jsonObjectKeyword1);
        //
        JSONObject jsonObjectKeyword2 = new JSONObject();
        jsonObjectKeyword2.put("value", customerName);
        jsonObjectKeyword2.put("color", "#173177");
        jsonObject2.put("keyword2", jsonObjectKeyword2);
        //
        JSONObject jsonObjectKeyword3 = new JSONObject();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jsonObjectKeyword3.put("value", simpleDateFormat.format(new Date()));
        jsonObjectKeyword3.put("color", "#173177");
        jsonObject2.put("keyword3", jsonObjectKeyword3);
        //
        JSONObject jsonObjectRemark = new JSONObject();
        jsonObjectRemark.put("value", "点击查看详情信息");
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

    public static boolean sendProcessApprovalMsgToUserAtNight(String customerName, UserInfo userInfo, int messageId, PendingApproval pendingApproval) {
        String template_id = Constant.processApproveRemindTemplateId;
        // todo 改成一个前端页面(晚上详情)，传messgeId、userCode参数给这个页面
        String url = "http://www.szwd.online:8009/#/approvalToday?messageId?" + messageId + "&userCode=" + userInfo.getUserId();
//        String url = "http://www.szwd.online/userInfo/userClickWxMessage/" + messageId;
        //
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("touser", userInfo.getOpenId());
        jsonObject.put("template_id", template_id);
        jsonObject.put("url", url);
        JSONObject jsonObject2 = new JSONObject();
        //
        String userName = userInfo.getUserName() == null ? userInfo.getUserId() : userInfo.getUserName();
        JSONObject jsonObjectFirst = new JSONObject();
        jsonObjectFirst.put("value", "您好，" + userName + "，今天您完成审批的单据汇总信息如下");
        jsonObjectFirst.put("color", "#173177");
        jsonObject2.put("first", jsonObjectFirst);
        //
        String keyword1Content = "今日已审核数：" + pendingApproval.getTotalCount() + "\n" + "待审/未审核数：" + pendingApproval.getAdcount();
        JSONObject jsonObjectKeyword1 = new JSONObject();
        jsonObjectKeyword1.put("value", keyword1Content);
        jsonObjectKeyword1.put("color", "#173177");
        jsonObject2.put("keyword1", jsonObjectKeyword1);
        //
        JSONObject jsonObjectKeyword2 = new JSONObject();
        jsonObjectKeyword2.put("value", customerName);
        jsonObjectKeyword2.put("color", "#173177");
        jsonObject2.put("keyword2", jsonObjectKeyword2);
        //
        JSONObject jsonObjectKeyword3 = new JSONObject();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jsonObjectKeyword3.put("value", simpleDateFormat.format(new Date()));
        jsonObjectKeyword3.put("color", "#173177");
        jsonObject2.put("keyword3", jsonObjectKeyword3);
        //
        JSONObject jsonObjectRemark = new JSONObject();
        jsonObjectRemark.put("value", "点击查看详情信息");
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


//    暂时用不上
//    public static boolean sendProcessApprovalMsgToUser(String openid, int messageId, int pendingApprovalNumber) {
//        String template_id = Constant.processApproveRemindTemplateId;
//        String url = "http://www.szwd.online/userInfo/userClickWxMessage/" + messageId;
//        //
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("touser", openid);
//        jsonObject.put("template_id", template_id);
//        jsonObject.put("url", url);
//        JSONObject jsonObject2 = new JSONObject();
//        //
//        JSONObject jsonObjectFirst = new JSONObject();
//        jsonObjectFirst.put("value", "你有" + pendingApprovalNumber + "个待审批事项");
//        jsonObjectFirst.put("color", "#173177");
//        jsonObject2.put("first", jsonObjectFirst);
//        //
//        JSONObject jsonObjectKeyword1 = new JSONObject();
//        jsonObjectKeyword1.put("value", "xxxxxx");
//        jsonObjectKeyword1.put("color", "#173177");
//        jsonObject2.put("keyword1", jsonObjectKeyword1);
//        //
//        JSONObject jsonObjectKeyword2 = new JSONObject();
//        jsonObjectKeyword2.put("value", "xxxxxx");
//        jsonObjectKeyword2.put("color", "#173177");
//        jsonObject2.put("keyword2", jsonObjectKeyword2);
//        //
//        JSONObject jsonObjectKeyword3 = new JSONObject();
//        jsonObjectKeyword3.put("value", "xxxxxx");
//        jsonObjectKeyword3.put("color", "#173177");
//        jsonObject2.put("keyword3", jsonObjectKeyword3);
//        //
//        JSONObject jsonObjectKeyword4 = new JSONObject();
//        jsonObjectKeyword4.put("value", "xxxxxx");
//        jsonObjectKeyword4.put("color", "#173177");
//        jsonObject2.put("keyword4", jsonObjectKeyword4);
//        //
//        JSONObject jsonObjectKeyword5 = new JSONObject();
//        jsonObjectKeyword5.put("value", "待审批事项详情请登录后台查看");
//        jsonObjectKeyword5.put("color", "#173177");
//        jsonObject2.put("keyword5", jsonObjectKeyword5);
//        //
//        JSONObject jsonObjectRemark = new JSONObject();
//        jsonObjectRemark.put("value", "点击查看详情阅读信息");
//        jsonObjectRemark.put("color", "#173177");
//        jsonObject2.put("remark", jsonObjectRemark);
//        //
//        jsonObject.put("data", jsonObject2);
//        //
//        String accessToken = WxUtil.getAccessToken();
//        String sendMsgUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s";
//        sendMsgUrl = String.format(sendMsgUrl, accessToken);
//        logger.info("sendMsgUrl:" + sendMsgUrl);
//        logger.info("jsonObject:" + jsonObject.toString());
//        JSONObject wxResult = WxUtil.postToWxServer(sendMsgUrl, jsonObject.toString());
//        String errcode = wxResult.getString("errcode");
//        String errmsg = wxResult.getString("errmsg");
//        String msgid = wxResult.getString("msgid");
//        logger.info("errcode:" + errcode + ", errmsg:" + errmsg + ", msgid:" + msgid);
//        if(errcode.equals("0")) {
//            return true;
//        } else {
//            return false;
//        }
//    }

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

    // 图片转byte数组
    public static byte[] imageToByte(String path) {
        byte[] data = null;
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet(path);
            HttpResponse response = httpClient.execute(httpGet);// 接收client执行的结果
            HttpEntity entity = response.getEntity();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            entity.writeTo(output);
            data = output.toByteArray();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("图片转二进制流失败");
            return null;
        }
        return data;
    }

    // byte数组转图片
    public static void byteToImage(byte[] data, String path) {
        if(data.length < 3 || path.equals("")) {
            logger.error("传入参数不合法");
            return;
        }
        try {
            FileImageOutputStream imageOutput = new FileImageOutputStream(new File(path));
            imageOutput.write(data, 0, data.length);
            imageOutput.close();
            logger.info("图片位置在：" + path);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("byte数组转图片失败");
        }
    }

    public static byte bit2byte(String bString){
        byte result=0;
        for(int i=bString.length()-1,j=0;i>=0;i--,j++){
            result+=(Byte.parseByte(bString.charAt(i)+"")*Math.pow(2, j));
        }
        return result;
    }


    public static byte[] Hex2Byte(String inHex) {

        String[] hex = inHex.split(" ");//将接收的字符串按空格分割成数组
        byte[] byteArray = new byte[hex.length];

        for (int i = 0; i < hex.length; i++) {
            //parseInt()方法用于将字符串参数作为有符号的n进制整数进行解析
            byteArray[i] = (byte) Integer.parseInt(hex[i], 16);
        }

        return byteArray;
    }

    public static String signEnCode(String pswd){
        byte XorKey[] = { (byte)0xB1, 0x23, (byte) 0xBB,0x13, (byte) 0x93,0x6D,0x44, 0x16};
        String str ="";
        String tmp = "";
        Integer j = 0;
        for (int i=0;i<pswd.length();i++){
            byte b = (byte)pswd.charAt(i);
            tmp = Integer.toHexString(b ^ XorKey[j]);
            str = str + tmp.substring(tmp.length()-2);
            j = (j+1) % 8;
        }
        return  str;
    }
}
