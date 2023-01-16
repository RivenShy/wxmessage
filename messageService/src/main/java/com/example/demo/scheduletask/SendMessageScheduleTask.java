package com.example.demo.scheduletask;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.util.WxUtil;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SendMessageScheduleTask {

    private static Logger logger = Logger.getLogger(SendMessageScheduleTask.class);

    public static final String template_id = "Co1-n9VISkwCUjCLjn34am3QCBSqY2SqIWCYzWHVJlE";;

    // 每天的10:42:00执行
//    @Scheduled(cron = "0 42 10 * * *")
    private void sendMessage() {
        System.out.println("scheduleTask Running ------");
    }

//    @Scheduled(cron = "0 47 11 * * *")
    private void sendMessage2() {
        System.out.println("sendMessage2 running");
        // 需要先绑定好公众号与网站用户
        // 查询用户，获取openID
        // 查询今天待审批的
        // 发送微信公众号推送消息
        String openid = "oa9m45sEj_dDR9FGoeoGmx7_M21o";
        String url = "http://weixin.qq.com/download";
        //
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("touser", openid);
        jsonObject.put("template_id", template_id);
        jsonObject.put("url", url);
        JSONObject jsonObject2 = new JSONObject();
        //
        JSONObject jsonObjectFirst = new JSONObject();
        jsonObjectFirst.put("value", "您有一个待审批事项");
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
        jsonObjectRemark.put("value", "回复 OK 直接批准");
        jsonObjectRemark.put("color", "#173177");
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

//    @Scheduled(cron = "0 29 11 * * *")
    private void sendMessage3() {
        // 需要先绑定好公众号与网站用户
        // 查询用户，获取openID
        // 查询今天已审批的，待审批的
    }

//    @Scheduled(cron = "0/5 * * * * *")
    private void testLog4j() throws InterruptedException {
//        BasicConfigurator.configure();
//        logger.setLevel(Level.DEBUG);
        logger.trace("跟踪信息");
        logger.debug("调试信息");
        logger.info("输出信息");
        Thread.sleep(1000);
        logger.warn("警告信息");
        logger.error("错误信息");
        logger.fatal("致命信息");
        System.out.println("yyds");
    }
}
