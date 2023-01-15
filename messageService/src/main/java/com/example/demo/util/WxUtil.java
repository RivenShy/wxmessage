package com.example.demo.util;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.controller.WxOfficialAccountController;
import com.example.demo.entity.*;
import com.github.pagehelper.StringUtil;
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
import org.springframework.util.FileCopyUtils;

import javax.imageio.stream.FileImageOutputStream;
import java.io.*;
import java.net.URLEncoder;
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
        if(!StringUtil.isEmpty(WxOfficialAccountController.accessTokenTest)) {
            return WxOfficialAccountController.accessTokenTest;
        }
        WxAccessToken wxAccessTokenCache = mapAccessToken.get(mapAccessTokenKey);
        if(wxAccessTokenCache != null) {
            Date now = new Date();
            if(now.before(wxAccessTokenCache.getExpireDate())){
                logger.info("缓存获取accessToken");
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
            logger.info("accessToken:" + accessToken);
            logger.info("expiresIn:" + expiresIn);
        } else {
            String errmsg = jsonObject.getString("errmsg");
            logger.info("getToken请求微信服务器失败，errcode:" + errcode + ",errmsg:" + errmsg);
            return null;
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
//        String template_id = "McTEif4kxJ-BNiLp1fMFYR8Ymzc5kJoujeIq1dhqL20";
        String template_id = Constant.outhTemplateId;
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
        String remarkMessage = "点击这里发起申请";
        JSONObject jsonObjectRemark = new JSONObject();
        jsonObjectRemark.put("value", remarkMessage);
//        jsonObjectRemark.put("color", "#173177");
        jsonObjectRemark.put("color", "#FF0000");
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

    // 授权绑定推送消息，一般很少做改动
    public static void sendCommonOuthMsgToUser(String openid, String outhUrl, String customerName, String userId, String userName, String serverIp, MessageTemplate messageTemplate) {
        String template_id = messageTemplate.getWxTemplateId();
//        String outhUrl = null;
//        if(!StringUtil.isEmpty(userId)) {
//            outhUrl = "http://www.szwd.online:8009/#/userAuthorization?serverIp=%s&userCode=%s&appid=%s";
//            outhUrl = String.format(outhUrl, serverIp, userId, WxOfficialAccountController.appid);
//        } else {
//            outhUrl = "http://www.szwd.online:8009/#/userAuthorizationByUserId?serverIp=%s&appid=%s";
//            outhUrl = String.format(outhUrl, serverIp, WxOfficialAccountController.appid);
//        }
        //
        String firstData = messageTemplate.getFirstData();
        String firstDataColor = messageTemplate.getFirstDataColor();
        String keyword1Data = messageTemplate.getKeyword1Data();
        String keyword1Color = messageTemplate.getKeyword1DataColor();
        String keyword2Data = messageTemplate.getKeyword2Data();
        String keyword2Color = messageTemplate.getKeyword2DataColor();
        String keyword3Data = messageTemplate.getKeyword1Data();
        String keyword3Color = messageTemplate.getKeyword1DataColor();
        String remarkData = messageTemplate.getRemarkData();
        String remarkDataColor = messageTemplate.getRemarkDataColor();
        //
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("touser", openid);
        jsonObject.put("template_id", template_id);
        jsonObject.put("url", outhUrl);
        JSONObject jsonObject2 = new JSONObject();
        //
        JSONObject jsonObjectFirst = new JSONObject();
        jsonObjectFirst.put("value", firstData);
        jsonObjectFirst.put("color", firstDataColor);
        jsonObject2.put("first", jsonObjectFirst);
        //
        String keyword1Content = userName == null ? userId : userName;
        keyword1Content = keyword1Content + "（" + customerName + "）";
        JSONObject jsonObjectKeyword1 = new JSONObject();
        jsonObjectKeyword1.put("value", keyword1Content);
        jsonObjectKeyword1.put("color", keyword1Color);
        jsonObject2.put("keyword1", jsonObjectKeyword1);
        //
        JSONObject jsonObjectKeyword2 = new JSONObject();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jsonObjectKeyword2.put("value", simpleDateFormat.format(new Date()));
        jsonObjectKeyword2.put("color", "#173177");
        jsonObject2.put("keyword2", jsonObjectKeyword2);
        //
        JSONObject jsonObjectRemark = new JSONObject();
        jsonObjectRemark.put("value", remarkData);
        jsonObjectRemark.put("color", remarkDataColor);
        jsonObject2.put("remark", jsonObjectRemark);
        //
        jsonObject.put("data", jsonObject2);
        //
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

    public static boolean sendProcessApprovalMsgToUser(String customerName, UserInfo userInfo, int messageId, AuditDelayCount auditDelayCount, Consultant consultant) {
        String template_id = Constant.processApproveRemindTemplateId;
        //  早上详情，传messgeId、userCode参数给这个页面
        String url = "http://www.szwd.online:8009/#/ApprovalInformation?messageId=" + messageId + "&userCode=" + userInfo.getUserId();
        //
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("touser", userInfo.getOpenId());
        jsonObject.put("template_id", template_id);
        jsonObject.put("url", url);
        JSONObject jsonObject2 = new JSONObject();
        //
//        String userName = userInfo.getUserName() == null ? userInfo.getUserId() : userInfo.getUserName();
        String userName = userInfo.getUserId();
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
        // 给实施顾问也推送同样的消息
        if(consultant != null && consultant.getOpenId() != null) {
            logger.info("给实施顾问推送消息");
            jsonObject.put("touser", consultant.getOpenId());
            jsonObjectFirst.put("value", "您好，" + userName + "，今天需要您审批的单据汇总信息如下【跟踪消息，实施顾问：" + consultant.getConsultCode() + "】");
            jsonObject2.put("first", jsonObjectFirst);
            jsonObject.put("data", jsonObject2);
            JSONObject wxResultConsultant = WxUtil.postToWxServer(sendMsgUrl, jsonObject.toString());
            if(!wxResult.getString("errcode").equals("0")) {
                logger.error("给顾问发送推送消息失败，errmsg = " + wxResultConsultant.getString("errmsg"));
            }
        }
        if(errcode.equals("0")) {
            return true;
        } else {
            logger.error("errcode:" + errcode + ", errmsg:" + errmsg + ", msgid:" + msgid);
            return false;
        }
    }

    /**
     *通用审批早消息模板
     */
    public static boolean sendCommonProcessApprovalMsgToUser(String customerName, UserInfo userInfo, int messageId, AuditDelayCount auditDelayCount, Consultant consultant, MessageTemplate messageTemplate) {
//        String template_id = Constant.processApproveRemindTemplateId;
        //  早上详情，传messgeId、userCode参数给这个页面
//        String url = "http://www.szwd.online:8009/#/ApprovalInformation?messageId=" + messageId + "&userCode=" + userInfo.getUserId();
        String url = messageTemplate.getDetailUrl();
        url = url.replaceAll("\\{messageId\\}",String.valueOf(messageId));
        url = url.replaceAll("\\{userCode\\}", userInfo.getUserId());
        String template_id = messageTemplate.getWxTemplateId();
        String firstData = messageTemplate.getFirstData();
        firstData = firstData.replaceAll("\\{userCode\\}", userInfo.getUserId());
        firstData = replaceProcessApprovalMsgPlaceholder(firstData, auditDelayCount);
        String firstDataColor = messageTemplate.getFirstDataColor();
        //
        String keyword1Data = messageTemplate.getKeyword1Data();
        keyword1Data = keyword1Data.replaceAll("\\{adcount\\}",String.valueOf(auditDelayCount.getAdcount()));
        keyword1Data = keyword1Data.replaceAll("\\{delaycount\\}", String.valueOf(auditDelayCount.getDelaycount()));
        keyword1Data = keyword1Data.replaceAll("/n","\n");
        keyword1Data = replaceProcessApprovalMsgPlaceholder(keyword1Data, auditDelayCount);
        String keyword1DataColor = messageTemplate.getKeyword1DataColor();
        //
        String keyword2Data = messageTemplate.getKeyword2Data();
        keyword2Data = keyword2Data.replaceAll("\\{customerName\\}",customerName);
        keyword2Data = replaceProcessApprovalMsgPlaceholder(keyword2Data, auditDelayCount);
        String keyword2DataColor = messageTemplate.getKeyword2DataColor();
        //
        String keyword3Data = messageTemplate.getKeyword3Data();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        keyword3Data = keyword3Data.replaceAll("\\{date\\}",simpleDateFormat.format(new Date()));
        keyword3Data = replaceProcessApprovalMsgPlaceholder(keyword3Data, auditDelayCount);
        String keyword3DataColor = messageTemplate.getKeyword3DataColor();
        //
        String remarkData = messageTemplate.getRemarkData();
        remarkData = replaceProcessApprovalMsgPlaceholder(remarkData, auditDelayCount);
        String remarkDataColor = messageTemplate.getRemarkDataColor();
        //
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("touser", userInfo.getOpenId());
        jsonObject.put("template_id", template_id);
        jsonObject.put("url", url);
        JSONObject jsonObject2 = new JSONObject();
        //
        JSONObject jsonObjectFirst = new JSONObject();
        jsonObjectFirst.put("value", firstData);
        jsonObjectFirst.put("color", firstDataColor);
        jsonObject2.put("first", jsonObjectFirst);
        //
//        String keyword1Content = "待审批单据总数：" + auditDelayCount.getAdcount() + "\n" + "已延期的单据数：" + auditDelayCount.getDelaycount();
        JSONObject jsonObjectKeyword1 = new JSONObject();
        jsonObjectKeyword1.put("value", keyword1Data);
        jsonObjectKeyword1.put("color", keyword1DataColor);
        jsonObject2.put("keyword1", jsonObjectKeyword1);
        //
        JSONObject jsonObjectKeyword2 = new JSONObject();
        jsonObjectKeyword2.put("value", keyword2Data);
        jsonObjectKeyword2.put("color", keyword2DataColor);
        jsonObject2.put("keyword2", jsonObjectKeyword2);
        //
        JSONObject jsonObjectKeyword3 = new JSONObject();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jsonObjectKeyword3.put("value", keyword3Data);
        jsonObjectKeyword3.put("color", keyword3DataColor);
        jsonObject2.put("keyword3", jsonObjectKeyword3);
        //
        JSONObject jsonObjectRemark = new JSONObject();
        jsonObjectRemark.put("value", remarkData);
        jsonObjectRemark.put("color", remarkDataColor);
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
        // 给实施顾问也推送同样的消息
        if(errcode.equals("0") && consultant != null && consultant.getOpenId() != null) {
            logger.info("给实施顾问推送消息");
            jsonObject.put("touser", consultant.getOpenId());
            jsonObjectFirst.put("value", firstData + "【跟踪消息，实施顾问：" + consultant.getConsultCode() + "】");
            jsonObjectFirst.put("color", firstDataColor);
            jsonObject2.put("first", jsonObjectFirst);
            jsonObject.put("data", jsonObject2);
            JSONObject wxResultConsultant = WxUtil.postToWxServer(sendMsgUrl, jsonObject.toString());
            if(!wxResult.getString("errcode").equals("0")) {
                logger.error("给顾问发送推送消息失败，errmsg = " + wxResultConsultant.getString("errmsg"));
            }
        }
        if(errcode.equals("0")) {
            return true;
        } else {
            logger.error("errcode:" + errcode + ", errmsg:" + errmsg + ", msgid:" + msgid);
            return false;
        }
    }

    public static boolean sendUrgentPendApprovalMsgToUser(String customerName, UserInfo userInfo, int messageId, PendingApprovalDetail pendingApprovalDetail, Consultant consultant) {
        String template_id = Constant.processApproveRemindTemplateId;
        //  早上详情，传messgeId、userCode参数给这个页面
        String url = "http://www.szwd.online:8009/#/ApprovalInformationUrgent?messageId=%s&auditName=%s&projName=%s&lastAuditTime=%s";
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String lastAuditTime = "";
            if(pendingApprovalDetail.getLastAuditTime() != null) {
                lastAuditTime = simpleDateFormat.format(pendingApprovalDetail.getLastAuditTime());
            }
            String projName = pendingApprovalDetail.getProjName() == null ? "": pendingApprovalDetail.getProjName();
            url = String.format(url, messageId, URLEncoder.encode(pendingApprovalDetail.getAuditName(),"UTF-8"), URLEncoder.encode(projName,"UTF-8"), URLEncoder.encode(lastAuditTime,"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("touser", userInfo.getOpenId());
        jsonObject.put("template_id", template_id);
        jsonObject.put("url", url);
        JSONObject jsonObject2 = new JSONObject();
        //
//        String userName = userInfo.getUserName() == null ? userInfo.getUserId() : userInfo.getUserName();
        String userName = userInfo.getUserId();
        JSONObject jsonObjectFirst = new JSONObject();
        jsonObjectFirst.put("value", "您好，" + userName + "，您有一条紧急待审批单据");
        jsonObjectFirst.put("color", "#173177");
        jsonObject2.put("first", jsonObjectFirst);
        //
        String keyword1Content = "待审批单据类型：" + pendingApprovalDetail.getAuditName() + "\n" + "推送原因：" + pendingApprovalDetail.getSendReason();
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
        // 给实施顾问也推送同样的消息
        if(errcode.equals("0") && consultant != null && consultant.getOpenId() != null) {
            logger.info("给实施顾问推送消息");
            jsonObject.put("touser", consultant.getOpenId());
            jsonObjectFirst.put("value", "您好，" + userName + "，您有一条紧急待审批单据【跟踪消息，实施顾问：" + consultant.getConsultCode() + "】");
            jsonObject2.put("first", jsonObjectFirst);
            jsonObject.put("data", jsonObject2);
            JSONObject wxResultConsultant = WxUtil.postToWxServer(sendMsgUrl, jsonObject.toString());
            if(!wxResult.getString("errcode").equals("0")) {
                logger.error("给顾问发送推送消息失败，errmsg = " + wxResultConsultant.getString("errmsg"));
            }
        }
        if(errcode.equals("0")) {
            return true;
        } else {
            logger.error("errcode:" + errcode + ", errmsg:" + errmsg + ", msgid:" + msgid);
            return false;
        }
    }


    public static boolean sendCommonUrgentPendApprovalMsgToUser(String customerName, UserInfo userInfo, int messageId, PendingApprovalDetail pendingApprovalDetail, Consultant consultant, MessageTemplate messageTemplate) {
        String template_id = messageTemplate.getWxTemplateId();
        String url = messageTemplate.getDetailUrl();
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String lastAuditTime = "";
            if(pendingApprovalDetail.getLastAuditTime() != null) {
                lastAuditTime = simpleDateFormat.format(pendingApprovalDetail.getLastAuditTime());
            }
            String projName = pendingApprovalDetail.getProjName() == null ? "": pendingApprovalDetail.getProjName();
            url = url.replaceAll("\\{messageId\\}", String.valueOf(messageId));
            url = url.replaceAll("\\{auditName\\}", URLEncoder.encode(pendingApprovalDetail.getAuditName(),"UTF-8"));
            url = url.replaceAll("\\{projName\\}", URLEncoder.encode(projName,"UTF-8"));
            url = url.replaceAll("\\{lastAuditTime\\}", URLEncoder.encode(lastAuditTime,"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //
        String firstData = messageTemplate.getFirstData();
        firstData = firstData.replaceAll("\\{userName\\}", userInfo.getUserId());
        String firstDataColor = messageTemplate.getFirstDataColor();
        //
        String keyword1Data = messageTemplate.getKeyword1Data();
        keyword1Data = keyword1Data.replaceAll("\\{AuditName\\}",pendingApprovalDetail.getAuditName());
        keyword1Data = keyword1Data.replaceAll("\\{sendReason\\}", pendingApprovalDetail.getSendReason());
        keyword1Data = keyword1Data.replaceAll("/n","\n");
        String keyword1DataColor = messageTemplate.getKeyword1DataColor();
        //
        String keyword2Data = messageTemplate.getKeyword2Data();
        keyword2Data = keyword2Data.replaceAll("\\{customerName\\}",customerName);
        String keyword2DataColor = messageTemplate.getKeyword2DataColor();
        //
        String keyword3Data = messageTemplate.getKeyword3Data();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        keyword3Data = keyword3Data.replaceAll("\\{date\\}",simpleDateFormat.format(new Date()));
        String keyword3DataColor = messageTemplate.getKeyword3DataColor();
        //
        String remarkData = messageTemplate.getRemarkData();
        String remarkDataColor = messageTemplate.getRemarkDataColor();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("touser", userInfo.getOpenId());
        jsonObject.put("template_id", template_id);
        jsonObject.put("url", url);
        JSONObject jsonObject2 = new JSONObject();
        //
        JSONObject jsonObjectFirst = new JSONObject();
        jsonObjectFirst.put("value", firstData);
        jsonObjectFirst.put("color", firstDataColor);
        jsonObject2.put("first", jsonObjectFirst);
        //
        JSONObject jsonObjectKeyword1 = new JSONObject();
        jsonObjectKeyword1.put("value", keyword1Data);
        jsonObjectKeyword1.put("color", keyword1DataColor);
        jsonObject2.put("keyword1", jsonObjectKeyword1);
        //
        JSONObject jsonObjectKeyword2 = new JSONObject();
        jsonObjectKeyword2.put("value", keyword2Data);
        jsonObjectKeyword2.put("color", keyword2DataColor);
        jsonObject2.put("keyword2", jsonObjectKeyword2);
        //
        JSONObject jsonObjectKeyword3 = new JSONObject();
        jsonObjectKeyword3.put("value", keyword3Data);
        jsonObjectKeyword3.put("color", keyword3DataColor);
        jsonObject2.put("keyword3", jsonObjectKeyword3);
        //
        JSONObject jsonObjectRemark = new JSONObject();
        jsonObjectRemark.put("value", remarkData);
        jsonObjectRemark.put("color", remarkDataColor);
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
        // 给实施顾问也推送同样的消息
        if(errcode.equals("0") && consultant != null && consultant.getOpenId() != null) {
            logger.info("给实施顾问推送消息");
            jsonObject.put("touser", consultant.getOpenId());
            jsonObjectFirst.put("value", firstData + "【跟踪消息，实施顾问：" + consultant.getConsultCode() + "】");
            jsonObject2.put("first", jsonObjectFirst);
            jsonObject.put("data", jsonObject2);
            JSONObject wxResultConsultant = WxUtil.postToWxServer(sendMsgUrl, jsonObject.toString());
            if(!wxResult.getString("errcode").equals("0")) {
                logger.error("给顾问发送推送消息失败，errmsg = " + wxResultConsultant.getString("errmsg"));
            }
        }
        if(errcode.equals("0")) {
            return true;
        } else {
            logger.error("errcode:" + errcode + ", errmsg:" + errmsg + ", msgid:" + msgid);
            return false;
        }
    }

    public static boolean sendProcessApprovalMsgToUserAtNight(String customerName, UserInfo userInfo, int messageId, PendingApproval pendingApproval, Consultant consultant) {
        String template_id = Constant.processApproveRemindTemplateId;
        // 晚上详情，传messgeId、userCode参数给这个页面
        String url = "http://www.szwd.online:8009/#/approvalToday?messageId=" + messageId + "&userCode=" + userInfo.getUserId();
        //
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("touser", userInfo.getOpenId());
        jsonObject.put("template_id", template_id);
        jsonObject.put("url", url);
        JSONObject jsonObject2 = new JSONObject();
        //
//        String userName = userInfo.getUserName() == null ? userInfo.getUserId() : userInfo.getUserName();
        String userName = userInfo.getUserId();

        JSONObject jsonObjectFirst = new JSONObject();
        jsonObjectFirst.put("value", "您好，" + userName + "，今天您完成审批的单据汇总信息如下");
        jsonObjectFirst.put("color", "#173177");
        jsonObject2.put("first", jsonObjectFirst);
        //
        String keyword1Content = "今日已审核数：" + pendingApproval.getTodayCount() + "\n" + "待审/未审核数：" + pendingApproval.getAdcount();
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
        // 给实施顾问也推送同样的消息
        if(consultant != null && consultant.getOpenId() != null) {
            logger.info("给实施顾问推送消息");
            jsonObject.put("touser", consultant.getOpenId());
            jsonObjectFirst.put("value", "您好，" + userName + "，今天您完成审批的单据汇总信息如下【跟踪消息，实施顾问：" + consultant.getConsultCode() + "】");
            jsonObject2.put("first", jsonObjectFirst);
            jsonObject.put("data", jsonObject2);
            JSONObject wxResultConsultant = WxUtil.postToWxServer(sendMsgUrl, jsonObject.toString());
            if (!wxResult.getString("errcode").equals("0")) {
                logger.error("给顾问发送推送消息失败，errmsg = " + wxResultConsultant.getString("errmsg"));
            }
        }
        logger.info("errcode:" + errcode + ", errmsg:" + errmsg + ", msgid:" + msgid);
        if(errcode.equals("0")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 通用审批晚消息模板
     */
    public static boolean sendCommonProcessApprovalMsgToUserAtNight(String customerName, UserInfo userInfo, int messageId, PendingApproval pendingApproval, Consultant consultant, MessageTemplate messageTemplate) {
//        String template_id = Constant.processApproveRemindTemplateId;
        // 晚上详情，传messgeId、userCode参数给这个页面
        String url = messageTemplate.getDetailUrl();
        url = url.replaceAll("\\{messageId\\}",String.valueOf(messageId));
        url = url.replaceAll("\\{userCode\\}", userInfo.getUserId());
        String template_id = messageTemplate.getWxTemplateId();
        String firstData = messageTemplate.getFirstData();
        firstData = firstData.replaceAll("\\{userCode\\}", userInfo.getUserId());
        firstData = replaceProcessApprovalMsgNightPlaceholder(firstData, pendingApproval);
        String firstDataColor = messageTemplate.getFirstDataColor();
        //
        String keyword1Data = messageTemplate.getKeyword1Data();
        keyword1Data = keyword1Data.replaceAll("\\{todayCount\\}",String.valueOf(pendingApproval.getTodayCount()));
        keyword1Data = keyword1Data.replaceAll("\\{adcount\\}", String.valueOf(pendingApproval.getAdcount()));
        keyword1Data = keyword1Data.replaceAll("/n","\n");
        keyword1Data = replaceProcessApprovalMsgNightPlaceholder(keyword1Data, pendingApproval);
        String keyword1DataColor = messageTemplate.getKeyword1DataColor();
        //
        String keyword2Data = messageTemplate.getKeyword2Data();
        keyword2Data = keyword2Data.replaceAll("\\{customerName\\}",customerName);
        keyword2Data = replaceProcessApprovalMsgNightPlaceholder(keyword2Data, pendingApproval);
        String keyword2DataColor = messageTemplate.getKeyword2DataColor();
        //
        String keyword3Data = messageTemplate.getKeyword3Data();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        keyword3Data = keyword3Data.replaceAll("\\{date\\}",simpleDateFormat.format(new Date()));
        keyword3Data = replaceProcessApprovalMsgNightPlaceholder(keyword3Data, pendingApproval);
        String keyword3DataColor = messageTemplate.getKeyword3DataColor();
        //
        String remarkData = messageTemplate.getRemarkData();
        remarkData = replaceProcessApprovalMsgNightPlaceholder(remarkData, pendingApproval);
        String remarkDataColor = messageTemplate.getRemarkDataColor();
        //
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("touser", userInfo.getOpenId());
        jsonObject.put("template_id", template_id);
//        jsonObject.put("emphasis_keyword", "keyword1.DATA");
        jsonObject.put("url", url);
        JSONObject jsonObject2 = new JSONObject();
        //
        JSONObject jsonObjectFirst = new JSONObject();
        jsonObjectFirst.put("value", firstData);
        jsonObjectFirst.put("color", firstDataColor);
        jsonObject2.put("first", jsonObjectFirst);
        //
//        String keyword1Content = "待审批单据总数：" + auditDelayCount.getAdcount() + "\n" + "已延期的单据数：" + auditDelayCount.getDelaycount();
        JSONObject jsonObjectKeyword1 = new JSONObject();
        jsonObjectKeyword1.put("value", keyword1Data);
        jsonObjectKeyword1.put("color", keyword1DataColor);
        jsonObject2.put("keyword1", jsonObjectKeyword1);
        //
        JSONObject jsonObjectKeyword2 = new JSONObject();
        jsonObjectKeyword2.put("value", keyword2Data);
        jsonObjectKeyword2.put("color", keyword2DataColor);
        jsonObject2.put("keyword2", jsonObjectKeyword2);
        //
        JSONObject jsonObjectKeyword3 = new JSONObject();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jsonObjectKeyword3.put("value", keyword3Data);
        jsonObjectKeyword3.put("color", keyword3DataColor);
        jsonObject2.put("keyword3", jsonObjectKeyword3);
        //
        JSONObject jsonObjectRemark = new JSONObject();
        jsonObjectRemark.put("value", remarkData);
        jsonObjectRemark.put("color", remarkDataColor);
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
        // 给实施顾问也推送同样的消息
        if(errcode.equals("0") && consultant != null && consultant.getOpenId() != null) {
            logger.info("给实施顾问推送消息");
            jsonObject.put("touser", consultant.getOpenId());
            jsonObjectFirst.put("value", firstData + "【跟踪消息，实施顾问：" + consultant.getConsultCode() + "】");
            jsonObjectFirst.put("color", firstDataColor);
            jsonObject2.put("first", jsonObjectFirst);
            jsonObject.put("data", jsonObject2);
            JSONObject wxResultConsultant = WxUtil.postToWxServer(sendMsgUrl, jsonObject.toString());
            if(!wxResult.getString("errcode").equals("0")) {
                logger.error("给顾问发送推送消息失败，errmsg = " + wxResultConsultant.getString("errmsg"));
            }
        }
        if(errcode.equals("0")) {
            return true;
        } else {
            logger.error("errcode:" + errcode + ", errmsg:" + errmsg + ", msgid:" + msgid);
            return false;
        }
    }

    public static boolean sendApprovalPass(String customerName, UserInfo userInfo, int messageId, ApprovalResult approvalResult, Consultant consultant, MessageTemplate messageTemplate, ApprovalConfig approvalConfig) {
        String url = messageTemplate.getDetailUrl();
        url = url.replaceAll("\\{messageId\\}",String.valueOf(messageId));
        url = url.replaceAll("\\{codeId\\}", approvalResult.getCodeid());
        String template_id = messageTemplate.getWxTemplateId();
        String firstData = messageTemplate.getFirstData();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        firstData = firstData.replaceAll("\\{auditTime\\}", simpleDateFormat.format(approvalResult.getAuditTime()));
        firstData = firstData.replaceAll("/n","\n");
        firstData = replaceApprovalPassPlaceholder(firstData, approvalResult, approvalConfig);
        String firstDataColor = messageTemplate.getFirstDataColor();
        //
        String keyword1Data = messageTemplate.getKeyword1Data();
        keyword1Data = keyword1Data.replaceAll("\\{auditName\\}", approvalResult.getAuditName());
        keyword1Data = replaceApprovalPassPlaceholder(keyword1Data, approvalResult, approvalConfig);
        String keyword1DataColor = messageTemplate.getKeyword1DataColor();
        //
        String keyword2Data = messageTemplate.getKeyword2Data();
        keyword2Data = keyword2Data.replaceAll("\\{subTime\\}", simpleDateFormat.format(approvalResult.getSubTime()));
        keyword2Data = replaceApprovalPassPlaceholder(keyword2Data, approvalResult, approvalConfig);
        String keyword2DataColor = messageTemplate.getKeyword2DataColor();
        //
        String keyword4Data = messageTemplate.getKeyword4Data();
        keyword4Data = replaceApprovalPassPlaceholder(keyword4Data, approvalResult, approvalConfig);
        String keyword4DataColor = messageTemplate.getKeyword4DataColor();
        //
        String keyword5Data = messageTemplate.getKeyword5Data();
        keyword5Data = keyword5Data.replaceAll("\\{auditTime\\}", simpleDateFormat.format(approvalResult.getAuditTime()));
        keyword5Data = replaceApprovalPassPlaceholder(keyword5Data, approvalResult, approvalConfig);
        String keyword5DataColor = messageTemplate.getKeyword5DataColor();
        //
        String keyword3Data = messageTemplate.getKeyword3Data();
        keyword3Data = replaceApprovalPassPlaceholder(keyword3Data, approvalResult, approvalConfig);
        String keyword3DataColor = messageTemplate.getKeyword3DataColor();
        //
        String remarkData = messageTemplate.getRemarkData();
        remarkData = remarkData.replaceAll("\\{numDesc\\}", approvalResult.getNumDesc());
        remarkData = remarkData.replaceAll("\\{numDescExplain\\}", approvalConfig.getNumDescExplain());
        remarkData = remarkData.replaceAll("\\{numDesc\\}", approvalResult.getNumDesc());
        remarkData = remarkData.replaceAll("\\{codeDescExplain\\}", approvalConfig.getCodeDescExplain());
        remarkData = remarkData.replaceAll("\\{codeDesc\\}", approvalResult.getCodeDesc());
        remarkData = remarkData.replaceAll("/n","\n");
        remarkData = replaceApprovalPassPlaceholder(remarkData, approvalResult, approvalConfig);
        String remarkDataColor = messageTemplate.getRemarkDataColor();
        //
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("touser", userInfo.getOpenId());
        jsonObject.put("template_id", template_id);
//        前端未做好，先不提供详情
//        jsonObject.put("url", url);
        JSONObject jsonObject2 = new JSONObject();
        //
//        String userName = userInfo.getUserId();
        JSONObject jsonObjectFirst = new JSONObject();
        jsonObjectFirst.put("value", firstData);
        jsonObjectFirst.put("color", firstDataColor);
        jsonObject2.put("first", jsonObjectFirst);
        //
        JSONObject jsonObjectKeyword1 = new JSONObject();
        jsonObjectKeyword1.put("value", keyword1Data);
        jsonObjectKeyword1.put("color", keyword1DataColor);
        jsonObject2.put("keyword1", jsonObjectKeyword1);
        //
        JSONObject jsonObjectKeyword2 = new JSONObject();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jsonObjectKeyword2.put("value", keyword2Data);
        jsonObjectKeyword2.put("color", keyword2DataColor);
        jsonObject2.put("keyword2", jsonObjectKeyword2);
        //
        JSONObject jsonObjectKeyword3 = new JSONObject();
        jsonObjectKeyword3.put("value", keyword3Data);
        jsonObjectKeyword3.put("color", keyword3DataColor);
        jsonObject2.put("keyword3", jsonObjectKeyword3);
        //
        JSONObject jsonObjectKeyword4 = new JSONObject();
        jsonObjectKeyword4.put("value", keyword4Data);
        jsonObjectKeyword4.put("color", keyword4DataColor);
        jsonObject2.put("keyword4", jsonObjectKeyword4);
        //
        JSONObject jsonObjectKeyword5 = new JSONObject();
        jsonObjectKeyword5.put("value", keyword5Data);
        jsonObjectKeyword5.put("color", keyword5DataColor);
        jsonObject2.put("keyword5", jsonObjectKeyword5);
        //
        JSONObject jsonObjectRemark = new JSONObject();
        jsonObjectRemark.put("value", remarkData);
        jsonObjectRemark.put("color", remarkDataColor);
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
        // 给实施顾问也推送同样的消息
        if(errcode.equals("0") && consultant != null && consultant.getOpenId() != null) {
            logger.info("给实施顾问推送消息");
            jsonObject.put("touser", consultant.getOpenId());
            jsonObjectFirst.put("value", firstData + "【跟踪消息，实施顾问：" + consultant.getConsultCode() + "】");
            jsonObject2.put("first", jsonObjectFirst);
            jsonObject.put("data", jsonObject2);
            JSONObject wxResultConsultant = WxUtil.postToWxServer(sendMsgUrl, jsonObject.toString());
            if (!wxResult.getString("errcode").equals("0")) {
                logger.error("给顾问发送推送消息失败，errmsg = " + wxResultConsultant.getString("errmsg"));
            }
        }
        logger.info("errcode:" + errcode + ", errmsg:" + errmsg + ", msgid:" + msgid);
        if(errcode.equals("0")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean sendApprovalReturn(String customerName, UserInfo userInfo, int messageId, ApprovalResult approvalResult, Consultant consultant, MessageTemplate messageTemplate, ApprovalConfig approvalConfig) {
        String url = messageTemplate.getDetailUrl();
        url = url.replaceAll("\\{messageId\\}",String.valueOf(messageId));
        url = url.replaceAll("\\{codeId\\}", approvalResult.getCodeid());
        String template_id = messageTemplate.getWxTemplateId();
        String firstData = messageTemplate.getFirstData();
        firstData = replaceApprovalReturnPlaceholder(firstData, approvalResult, approvalConfig);
        String firstDataColor = messageTemplate.getFirstDataColor();
        //
        String keyword1Data = messageTemplate.getKeyword1Data();
        keyword1Data = keyword1Data.replaceAll("\\{auditName\\}", approvalResult.getAuditName());
        keyword1Data = replaceApprovalReturnPlaceholder(keyword1Data, approvalResult, approvalConfig);
        String keyword1DataColor = messageTemplate.getKeyword1DataColor();
        //
        String keyword2Data = messageTemplate.getKeyword2Data();
        keyword2Data = keyword2Data.replaceAll("\\{userName\\}",approvalResult.getUserName());
        keyword2Data = replaceApprovalReturnPlaceholder(keyword2Data, approvalResult, approvalConfig);
        String keyword2DataColor = messageTemplate.getKeyword2DataColor();
        //
        String keyword3Data = messageTemplate.getKeyword3Data();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        keyword3Data = keyword3Data.replaceAll("\\{auditTime\\}",simpleDateFormat.format(approvalResult.getAuditTime()));
        keyword3Data = replaceApprovalReturnPlaceholder(keyword3Data, approvalResult, approvalConfig);
        String keyword3DataColor = messageTemplate.getKeyword3DataColor();
        String keyword4Data = messageTemplate.getKeyword4Data();
        keyword4Data = keyword4Data.replaceAll("\\{auditMemo\\}", approvalResult.getAuditMemo());
        keyword4Data = replaceApprovalReturnPlaceholder(keyword4Data, approvalResult, approvalConfig);
        String keyword4DataColor = messageTemplate.getKeyword4DataColor();
        //
        String remarkData = messageTemplate.getRemarkData();
        remarkData = remarkData.replaceAll("\\{numDesc\\}", approvalResult.getNumDesc());
        remarkData = remarkData.replaceAll("\\{numDescExplain\\}", approvalConfig.getNumDescExplain());
        remarkData = remarkData.replaceAll("\\{numDesc\\}", approvalResult.getNumDesc());
        remarkData = remarkData.replaceAll("\\{codeDescExplain\\}", approvalConfig.getCodeDescExplain());
        remarkData = remarkData.replaceAll("\\{codeDesc\\}", approvalResult.getCodeDesc());
        remarkData = remarkData.replaceAll("/n","\n");
        remarkData = replaceApprovalReturnPlaceholder(remarkData, approvalResult, approvalConfig);
        String remarkDataColor = messageTemplate.getRemarkDataColor();
        //
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("touser", userInfo.getOpenId());
        jsonObject.put("template_id", template_id);
//        前端未做好，先不提供详情
//        jsonObject.put("url", url);
        JSONObject jsonObject2 = new JSONObject();
        //
//        String userName = userInfo.getUserId();
        JSONObject jsonObjectFirst = new JSONObject();
        jsonObjectFirst.put("value", firstData);
        jsonObjectFirst.put("color", firstDataColor);
        jsonObject2.put("first", jsonObjectFirst);
        //
        JSONObject jsonObjectKeyword1 = new JSONObject();
        jsonObjectKeyword1.put("value", keyword1Data);
        jsonObjectKeyword1.put("color", keyword1DataColor);
        jsonObject2.put("keyword1", jsonObjectKeyword1);
        //
        JSONObject jsonObjectKeyword2 = new JSONObject();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jsonObjectKeyword2.put("value", keyword2Data);
        jsonObjectKeyword2.put("color", keyword2DataColor);
        jsonObject2.put("keyword2", jsonObjectKeyword2);
        //
        JSONObject jsonObjectKeyword3 = new JSONObject();
        jsonObjectKeyword3.put("value", keyword3Data);
        jsonObjectKeyword3.put("color", keyword3DataColor);
        jsonObject2.put("keyword3", jsonObjectKeyword3);
        //
        JSONObject jsonObjectKeyword4 = new JSONObject();
        jsonObjectKeyword4.put("value", keyword4Data);
        jsonObjectKeyword4.put("color", keyword4DataColor);
        jsonObject2.put("keyword4", jsonObjectKeyword4);
        //
        JSONObject jsonObjectRemark = new JSONObject();
        jsonObjectRemark.put("value", remarkData);
        jsonObjectRemark.put("color", remarkDataColor);
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
        // 给实施顾问也推送同样的消息
        if(errcode.equals("0") && consultant != null && consultant.getOpenId() != null) {
            logger.info("给实施顾问推送消息");
            jsonObject.put("touser", consultant.getOpenId());
            jsonObjectFirst.put("value", firstData + "【跟踪消息，实施顾问：" + consultant.getConsultCode() + "】");
            jsonObject2.put("first", jsonObjectFirst);
            jsonObject.put("data", jsonObject2);
            JSONObject wxResultConsultant = WxUtil.postToWxServer(sendMsgUrl, jsonObject.toString());
            if (!wxResult.getString("errcode").equals("0")) {
                logger.error("给顾问发送推送消息失败，errmsg = " + wxResultConsultant.getString("errmsg"));
            }
        }
        logger.info("errcode:" + errcode + ", errmsg:" + errmsg + ", msgid:" + msgid);
        if(errcode.equals("0")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean sendWarehousingNotice(String customerName, UserInfo userInfo, int messageId, WarehouseNotice warehouseNotice, Consultant consultant, MessageTemplate messageTemplate) {
        // todo 入库详情页面
        String url = messageTemplate.getDetailUrl();
        url = url.replaceAll("\\{messageId\\}",String.valueOf(messageId));
        url = url.replaceAll("\\{docEntry\\}", warehouseNotice.getDocEntry());
        String template_id = messageTemplate.getWxTemplateId();
        String firstData = messageTemplate.getFirstData();
        firstData = firstData.replaceAll("\\{userName\\}", warehouseNotice.getStorePer());
        firstData = firstData.replaceAll("\\{projectName\\}", warehouseNotice.getProjName());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        int dayDiff = differentDaysByMillisecond(new Date(), warehouseNotice.getDocDate());
        firstData = firstData.replaceAll("\\{day\\}", String.valueOf(dayDiff));
        firstData = replaceWarehousingNoticePlaceholder(firstData, warehouseNotice);
        String firstDataColor = messageTemplate.getFirstDataColor();
        //
        String keyword1Data = messageTemplate.getKeyword1Data();
        keyword1Data = replaceWarehousingNoticePlaceholder(keyword1Data, warehouseNotice);
        String keyword1DataColor = messageTemplate.getKeyword1DataColor();
        //
        String keyword2Data = messageTemplate.getKeyword2Data();
        keyword2Data = keyword2Data.replaceAll("\\{date\\}", simpleDateFormat.format(warehouseNotice.getDocDate()));
        keyword2Data = replaceWarehousingNoticePlaceholder(keyword2Data, warehouseNotice);
        String keyword2DataColor = messageTemplate.getKeyword2DataColor();
        //
        String keyword3Data = messageTemplate.getKeyword3Data();
        keyword3Data = keyword3Data.replaceAll("\\{customerName\\}",customerName);
        keyword3Data = replaceWarehousingNoticePlaceholder(keyword3Data, warehouseNotice);
        String keyword3DataColor = messageTemplate.getKeyword3DataColor();
        //
        String remarkData = messageTemplate.getRemarkData();
        remarkData = remarkData.replaceAll("\\{shopperName\\}",warehouseNotice.getShopperName());
        remarkData = remarkData.replaceAll("\\{vedName\\}",warehouseNotice.getVedName());
        remarkData = remarkData.replaceAll("/n","\n");
        remarkData = replaceWarehousingNoticePlaceholder(remarkData, warehouseNotice);
        String remarkDataColor = messageTemplate.getRemarkDataColor();
        //
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("touser", userInfo.getOpenId());
        jsonObject.put("template_id", template_id);
        jsonObject.put("url", url);
        JSONObject jsonObject2 = new JSONObject();
        //
//        String userName = userInfo.getUserId();
        JSONObject jsonObjectFirst = new JSONObject();
        jsonObjectFirst.put("value", firstData);
        jsonObjectFirst.put("color", firstDataColor);
        jsonObject2.put("first", jsonObjectFirst);
        //
        JSONObject jsonObjectKeyword1 = new JSONObject();
        jsonObjectKeyword1.put("value", keyword1Data);
        jsonObjectKeyword1.put("color", keyword1DataColor);
        jsonObject2.put("keyword1", jsonObjectKeyword1);
        //
        JSONObject jsonObjectKeyword2 = new JSONObject();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jsonObjectKeyword2.put("value", keyword2Data);
        jsonObjectKeyword2.put("color", keyword2DataColor);
        jsonObject2.put("keyword2", jsonObjectKeyword2);
        //
        JSONObject jsonObjectKeyword3 = new JSONObject();
        jsonObjectKeyword3.put("value", keyword3Data);
        jsonObjectKeyword3.put("color", keyword3DataColor);
        jsonObject2.put("keyword3", jsonObjectKeyword3);
        //
        JSONObject jsonObjectRemark = new JSONObject();
        jsonObjectRemark.put("value", remarkData);
        jsonObjectRemark.put("color", remarkDataColor);
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
        // 给实施顾问也推送同样的消息
        if(errcode.equals("0") && consultant != null && consultant.getOpenId() != null) {
            logger.info("给实施顾问推送消息");
            jsonObject.put("touser", consultant.getOpenId());
            jsonObjectFirst.put("value", firstData + "【跟踪消息，实施顾问：" + consultant.getConsultCode() + "】");
            jsonObject2.put("first", jsonObjectFirst);
            jsonObject.put("data", jsonObject2);
            JSONObject wxResultConsultant = WxUtil.postToWxServer(sendMsgUrl, jsonObject.toString());
            if (!wxResult.getString("errcode").equals("0")) {
                logger.error("给顾问发送推送消息失败，errmsg = " + wxResultConsultant.getString("errmsg"));
            }
        }
        logger.info("errcode:" + errcode + ", errmsg:" + errmsg + ", msgid:" + msgid);
        if(errcode.equals("0")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean sendApprovalTimeout(String customerName, UserInfo userInfo, int messageId, ApprovalTimeout approvalTimeout, Consultant consultant, MessageTemplate messageTemplate, ApprovalConfig approvalConfig) {
//        String url = messageTemplate.getDetailUrl();
//        url = url.replaceAll("\\{messageId\\}",String.valueOf(messageId));
//        url = url.replaceAll("\\{docEntry\\}", warehouseNotice.getDocEntry());
        String template_id = messageTemplate.getWxTemplateId();
        String firstData = messageTemplate.getFirstData();
        firstData = firstData.replaceAll("\\{codeDesc\\}", approvalTimeout.getCodeDesc());
        String firstDataColor = messageTemplate.getFirstDataColor();
        //
        String keyword1Data = messageTemplate.getKeyword1Data();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        keyword1Data = keyword1Data.replaceAll("\\{date\\}", simpleDateFormat.format(new Date()));

        String keyword1DataColor = messageTemplate.getKeyword1DataColor();
        //
        String keyword2Data = messageTemplate.getKeyword2Data();
        String timeOut = getApproveTimeoutDayAndHour(approvalTimeout.getLastAuditTime(), new Date());
        keyword2Data = keyword2Data.replaceAll("\\{timeOut\\}", timeOut);
        String keyword2DataColor = messageTemplate.getKeyword2DataColor();
        //
        String keyword3Data = messageTemplate.getKeyword3Data();
        keyword3Data = keyword3Data.replaceAll("\\{auditName\\}",approvalTimeout.getAuditName());
        String keyword3DataColor = messageTemplate.getKeyword3DataColor();
        //
        String remarkData = messageTemplate.getRemarkData();
        remarkData = remarkData.replaceAll("\\{numDescExplain\\}", approvalConfig.getNumDescExplain());
        remarkData = remarkData.replaceAll("\\{numDesc\\}", approvalTimeout.getNumDesc());
        remarkData = remarkData.replaceAll("\\{codeDescExplain\\}", approvalConfig.getCodeDescExplain());
        remarkData = remarkData.replaceAll("\\{codeDesc\\}", approvalTimeout.getCodeDesc());
        remarkData = remarkData.replaceAll("/n","\n");
        String remarkDataColor = messageTemplate.getRemarkDataColor();
        //
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("touser", userInfo.getOpenId());
        jsonObject.put("template_id", template_id);
//        jsonObject.put("url", url);
        JSONObject jsonObject2 = new JSONObject();
        //
//        String userName = userInfo.getUserId();
        JSONObject jsonObjectFirst = new JSONObject();
        jsonObjectFirst.put("value", firstData);
        jsonObjectFirst.put("color", firstDataColor);
        jsonObject2.put("first", jsonObjectFirst);
        //
        JSONObject jsonObjectKeyword1 = new JSONObject();
        jsonObjectKeyword1.put("value", keyword1Data);
        jsonObjectKeyword1.put("color", keyword1DataColor);
        jsonObject2.put("keyword1", jsonObjectKeyword1);
        //
        JSONObject jsonObjectKeyword2 = new JSONObject();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jsonObjectKeyword2.put("value", keyword2Data);
        jsonObjectKeyword2.put("color", keyword2DataColor);
        jsonObject2.put("keyword2", jsonObjectKeyword2);
        //
        JSONObject jsonObjectKeyword3 = new JSONObject();
        jsonObjectKeyword3.put("value", keyword3Data);
        jsonObjectKeyword3.put("color", keyword3DataColor);
        jsonObject2.put("keyword3", jsonObjectKeyword3);
        //
        JSONObject jsonObjectRemark = new JSONObject();
        jsonObjectRemark.put("value", remarkData);
        jsonObjectRemark.put("color", remarkDataColor);
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
        // 给实施顾问也推送同样的消息
        if(errcode.equals("0") && consultant != null && consultant.getOpenId() != null) {
            logger.info("给实施顾问推送消息");
            jsonObject.put("touser", consultant.getOpenId());
            jsonObjectFirst.put("value", firstData + "【跟踪消息，实施顾问：" + consultant.getConsultCode() + "】");
            jsonObject2.put("first", jsonObjectFirst);
            jsonObject.put("data", jsonObject2);
            JSONObject wxResultConsultant = WxUtil.postToWxServer(sendMsgUrl, jsonObject.toString());
            if (!wxResult.getString("errcode").equals("0")) {
                logger.error("给顾问发送推送消息失败，errmsg = " + wxResultConsultant.getString("errmsg"));
            }
        }
        logger.info("errcode:" + errcode + ", errmsg:" + errmsg + ", msgid:" + msgid);
        if(errcode.equals("0")) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 入库通知占位符
     */
    public static String replaceWarehousingNoticePlaceholder(String originString, WarehouseNotice warehouseNotice) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        originString = originString.replaceAll("\\{ProjName\\}", warehouseNotice.getProjName());
        originString = originString.replaceAll("\\{DocDate\\}", simpleDateFormat.format(warehouseNotice.getDocDate()));
        originString = originString.replaceAll("\\{ShopperName\\}", warehouseNotice.getShopperName());
        originString = originString.replaceAll("\\{VedName\\}", warehouseNotice.getVedName());
        originString = originString.replaceAll("\\{StorePerId\\}", warehouseNotice.getStorePerId());
        originString = originString.replaceAll("\\{StorePer\\}", warehouseNotice.getStorePer());
        originString = originString.replaceAll("/n","\n");
        originString = originString.replaceAll("\\{docEntry\\}", warehouseNotice.getDocEntry());
        return originString;
    }

    /**
     * 审批通过占位符
     */
    public static String replaceApprovalPassPlaceholder(String originString, ApprovalResult approvalResult, ApprovalConfig approvalConfig) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        originString = originString.replaceAll("\\{Codeid\\}", approvalResult.getCodeid());
        originString = originString.replaceAll("\\{SubMitUser\\}", approvalResult.getSubMitUser());
        originString = originString.replaceAll("\\{SubTime\\}", simpleDateFormat.format(approvalResult.getSubTime()));
        originString = originString.replaceAll("\\{Status\\}", approvalResult.getStatus());
        originString = originString.replaceAll("\\{AuditName\\}", approvalResult.getAuditName());
        originString = originString.replaceAll("\\{AuditTime\\}", simpleDateFormat.format(approvalResult.getAuditTime()));
        originString = originString.replaceAll("\\{CodeDesc\\}", approvalResult.getCodeDesc());
        originString = originString.replaceAll("/n","\n");
        originString = originString.replaceAll("\\{NumDesc\\}", approvalResult.getNumDesc());
        originString = originString.replaceAll("\\{UserName\\}", approvalResult.getUserName());
        originString = originString.replaceAll("\\{AuditMemo\\}", approvalResult.getAuditMemo());
        originString = originString.replaceAll("\\{numDescExplain\\}", approvalConfig.getNumDescExplain());
        originString = originString.replaceAll("\\{codeDescExplain\\}", approvalConfig.getCodeDescExplain());
        return originString;
    }

    /**
     * 审批否决占位符
     */
    public static String replaceApprovalReturnPlaceholder(String originString, ApprovalResult approvalResult, ApprovalConfig approvalConfig) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        originString = originString.replaceAll("\\{Codeid\\}", approvalResult.getCodeid());
        originString = originString.replaceAll("\\{SubMitUser\\}", approvalResult.getSubMitUser());
        originString = originString.replaceAll("\\{SubTime\\}", simpleDateFormat.format(approvalResult.getSubTime()));
        originString = originString.replaceAll("\\{Status\\}", approvalResult.getStatus());
        originString = originString.replaceAll("\\{AuditName\\}", approvalResult.getAuditName());
        originString = originString.replaceAll("\\{AuditTime\\}", simpleDateFormat.format(approvalResult.getAuditTime()));
        originString = originString.replaceAll("\\{CodeDesc\\}", approvalResult.getCodeDesc());
        originString = originString.replaceAll("/n","\n");
        originString = originString.replaceAll("\\{NumDesc\\}", approvalResult.getNumDesc());
        originString = originString.replaceAll("\\{UserName\\}", approvalResult.getUserName());
        originString = originString.replaceAll("\\{AuditMemo\\}", approvalResult.getAuditMemo());
        originString = originString.replaceAll("\\{numDescExplain\\}", approvalConfig.getNumDescExplain());
        originString = originString.replaceAll("\\{codeDescExplain\\}", approvalConfig.getCodeDescExplain());
        return originString;
    }

    public static String replaceApprovalTimeoutPlaceholder(String originString, ApprovalTimeout approvalTimeout, ApprovalConfig approvalConfig) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        originString = originString.replaceAll("\\{codeid\\}", approvalTimeout.getCodeid());
        originString = originString.replaceAll("\\{SubTime\\}", simpleDateFormat.format(approvalTimeout.getSubTime()));
        originString = originString.replaceAll("\\{AuditName\\}", approvalTimeout.getAuditName());
        originString = originString.replaceAll("\\{lastAuditTime\\}", simpleDateFormat.format(approvalTimeout.getLastAuditTime()));
        originString = originString.replaceAll("\\{jobuser\\}", approvalTimeout.getJobuser());
        originString = originString.replaceAll("\\{CodeDesc\\}", approvalTimeout.getCodeDesc());
        originString = originString.replaceAll("\\{NumDesc\\}", approvalTimeout.getNumDesc());
        originString = originString.replaceAll("/n","\n");
        originString = originString.replaceAll("\\{numDescExplain\\}", approvalConfig.getNumDescExplain());
        originString = originString.replaceAll("\\{codeDescExplain\\}", approvalConfig.getCodeDescExplain());
        return originString;
    }

    /**
     * 早消息占位符替换
     */
    public static String replaceProcessApprovalMsgPlaceholder(String originString, AuditDelayCount auditDelayCount) {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        originString = originString.replaceAll("\\{jobuser\\}", auditDelayCount.getJobuser());
        originString = originString.replaceAll("\\{adcount\\}", String.valueOf(auditDelayCount.getAdcount()));
        originString = originString.replaceAll("\\{delaycount\\}", String.valueOf(auditDelayCount.getDelaycount()));
        originString = originString.replaceAll("/n","\n");
        return originString;
    }

    /**
     * 晚消息占位符替换
     */
    public static String replaceProcessApprovalMsgNightPlaceholder(String originString, PendingApproval pendingApproval) {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        originString = originString.replaceAll("\\{jobuser\\}", pendingApproval.getJobuser());
        originString = originString.replaceAll("\\{todayCount\\}", String.valueOf(pendingApproval.getTodayCount()));
        originString = originString.replaceAll("\\{adcount\\}", String.valueOf(pendingApproval.getAdcount()));
        originString = originString.replaceAll("/n","\n");
        return originString;
    }


    public static boolean sendCommonApprovalTimeout(String customerName, UserInfo userInfo, int messageId, ApprovalTimeout approvalTimeout, Consultant consultant, MessageTemplate messageTemplate, ApprovalConfig approvalConfig) {
//        String url = messageTemplate.getDetailUrl();
//        url = url.replaceAll("\\{messageId\\}",String.valueOf(messageId));
//        url = url.replaceAll("\\{docEntry\\}", warehouseNotice.getDocEntry());
        String template_id = messageTemplate.getWxTemplateId();
        String firstData = messageTemplate.getFirstData();
//        firstData = firstData.replaceAll("\\{codeDesc\\}", approvalTimeout.getCodeDesc());
        firstData = replaceApprovalTimeoutPlaceholder(firstData, approvalTimeout, approvalConfig);
        String firstDataColor = messageTemplate.getFirstDataColor();
        //
        String keyword1Data = messageTemplate.getKeyword1Data();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        keyword1Data = keyword1Data.replaceAll("\\{date\\}", simpleDateFormat.format(new Date()));
        keyword1Data = replaceApprovalTimeoutPlaceholder(keyword1Data, approvalTimeout, approvalConfig);
        String keyword1DataColor = messageTemplate.getKeyword1DataColor();
        //
        String keyword2Data = messageTemplate.getKeyword2Data();
        String timeOut = getApproveTimeoutDayAndHour(approvalTimeout.getLastAuditTime(), new Date());
        keyword2Data = keyword2Data.replaceAll("\\{timeOut\\}", timeOut);
        keyword2Data = replaceApprovalTimeoutPlaceholder(keyword2Data, approvalTimeout, approvalConfig);
        String keyword2DataColor = messageTemplate.getKeyword2DataColor();
        //
        String keyword3Data = messageTemplate.getKeyword3Data();
//        keyword3Data = keyword3Data.replaceAll("\\{auditName\\}",approvalTimeout.getAuditName());
        keyword3Data = replaceApprovalTimeoutPlaceholder(keyword3Data, approvalTimeout, approvalConfig);
        String keyword3DataColor = messageTemplate.getKeyword3DataColor();
        //
        String remarkData = messageTemplate.getRemarkData();
        remarkData = remarkData.replaceAll("\\{numDescExplain\\}", approvalConfig.getNumDescExplain());
//        remarkData = remarkData.replaceAll("\\{numDesc\\}", approvalTimeout.getNumDesc());
        remarkData = remarkData.replaceAll("\\{codeDescExplain\\}", approvalConfig.getCodeDescExplain());
//        remarkData = remarkData.replaceAll("\\{codeDesc\\}", approvalTimeout.getCodeDesc());
        remarkData = remarkData.replaceAll("/n","\n");
        remarkData = replaceApprovalTimeoutPlaceholder(remarkData, approvalTimeout, approvalConfig);
        String remarkDataColor = messageTemplate.getRemarkDataColor();
        //
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("touser", userInfo.getOpenId());
        jsonObject.put("template_id", template_id);
//        jsonObject.put("url", url);
        JSONObject jsonObject2 = new JSONObject();
        //
//        String userName = userInfo.getUserId();
        JSONObject jsonObjectFirst = new JSONObject();
        jsonObjectFirst.put("value", firstData);
        jsonObjectFirst.put("color", firstDataColor);
        jsonObject2.put("first", jsonObjectFirst);
        //
        JSONObject jsonObjectKeyword1 = new JSONObject();
        jsonObjectKeyword1.put("value", keyword1Data);
        jsonObjectKeyword1.put("color", keyword1DataColor);
        jsonObject2.put("keyword1", jsonObjectKeyword1);
        //
        JSONObject jsonObjectKeyword2 = new JSONObject();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jsonObjectKeyword2.put("value", keyword2Data);
        jsonObjectKeyword2.put("color", keyword2DataColor);
        jsonObject2.put("keyword2", jsonObjectKeyword2);
        //
        JSONObject jsonObjectKeyword3 = new JSONObject();
        jsonObjectKeyword3.put("value", keyword3Data);
        jsonObjectKeyword3.put("color", keyword3DataColor);
        jsonObject2.put("keyword3", jsonObjectKeyword3);
        //
        JSONObject jsonObjectRemark = new JSONObject();
        jsonObjectRemark.put("value", remarkData);
        jsonObjectRemark.put("color", remarkDataColor);
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
        // 给实施顾问也推送同样的消息
        if(errcode.equals("0") && consultant != null && consultant.getOpenId() != null) {
            logger.info("给实施顾问推送消息");
            jsonObject.put("touser", consultant.getOpenId());
            jsonObjectFirst.put("value", firstData + "【跟踪消息，实施顾问：" + consultant.getConsultCode() + "】");
            jsonObject2.put("first", jsonObjectFirst);
            jsonObject.put("data", jsonObject2);
            JSONObject wxResultConsultant = WxUtil.postToWxServer(sendMsgUrl, jsonObject.toString());
            if (!wxResult.getString("errcode").equals("0")) {
                logger.error("给顾问发送推送消息失败，errmsg = " + wxResultConsultant.getString("errmsg"));
            }
        }
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
        //
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

    /**
     * 文件转为二进制数组
     * @param file
     * @return
     */
    public static byte[] fileToBinArray(File file){
        try {
            InputStream fis = new FileInputStream(file);
            byte[] bytes = FileCopyUtils.copyToByteArray(fis);
            return bytes;
        }catch (Exception ex){
            throw new RuntimeException("transform file into bin Array 出错",ex);
        }
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
//        byte XorKey[] = {-79, 35, -69, 19};
        String str ="";
        String tmp = "";
        Integer j = 0;
        for (int i=0;i<pswd.length();i++){
            byte b = (byte)pswd.charAt(i);
            // xxxxx11/01 与 b 异或运算,结果大于0
            tmp = Integer.toHexString(b ^ XorKey[j]);
            str = str + tmp.substring(tmp.length()-2); // 取tmp后两位
            j = (j+1) % 8;
        }
        return  str;
    }

    public static boolean checkGuidAuth(String guid) {
        String guidTail2 = guid.substring(guid.length() - 2);
        guidTail2 = guidTail2.toUpperCase();
        String guidTail2EnCode = WxUtil.signEnCode(guidTail2);
        String guidHeadEncode = guid.substring(0, guidTail2EnCode.length());
        return guidHeadEncode.equals(guidTail2EnCode.toUpperCase());
    }

    /**
     * 相差天数计算
     */
    public static int differentDaysByMillisecond(Date date1, Date date2) {
//        return Math.abs((int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24)));
        return (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
    }

    public static String getApproveTimeoutDayAndHour(Date date1, Date date2) {
//        return Math.abs((int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24)));
//        return (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
        int day = (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
        if(day > 0) {
            int restHour = (int) ((date2.getTime() - date1.getTime()) % (1000 * 3600 * 24) / (1000 * 3600));
            return day + "天" + restHour + "小时";
        } else {
            int hour = (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600));
            return hour + "小时";
        }
    }
}
