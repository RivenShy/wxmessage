package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.config.ScheduleConfig;
import com.example.demo.entity.Message;
import com.example.demo.entity.MessageType;
import com.example.demo.entity.Server;
import com.example.demo.entity.UserInfo;
import com.example.demo.service.MessageService;
import com.example.demo.service.ServerService;
import com.example.demo.service.UserInfoService;
import com.example.demo.util.Constant;
import com.example.demo.util.WxUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/userInfo")
public class UserInfoController {

    private static Logger logger = Logger.getLogger(UserInfoController.class);

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private ServerService serverService;

    @Autowired
    private MessageService messageService;

    @GetMapping("/listUserInfo")
    public ModelAndView listUserInfo() {
        return new ModelAndView("listUserInfo");
    }

    @GetMapping("/sendMsgToUser")
    public ModelAndView sendMsgToUser() {
        return new ModelAndView("sendMsgToUser");
    }

    @GetMapping("/userInfos")
    public PageInfo<UserInfo> list(@RequestParam(value = "start", defaultValue = "1") int start, @RequestParam(value = "size", defaultValue = "5") int size) throws Exception {
        PageHelper.startPage(start,size,"id desc");
        List<UserInfo> hs = userInfoService.list();
//        System.out.println(hs.size());

        PageInfo<UserInfo> page = new PageInfo<>(hs,5);

        return page;
    }

    @ResponseBody
//    @GetMapping("/getQRcodeByServerIdAndUserId/{serverId}/{userId}")
    @PostMapping("/getQRcodeByServerIdAndUserId")
    public String getQRcodeByServerIdAndUserId(@RequestBody UserInfo userInfo) {
        logger.info("getQRcode");
        logger.info(userInfo);
        int serverId = userInfo.getServerId();
        String userId = userInfo.getUserId();
        JSONObject jsonObjectReturn = new JSONObject();
        if(userId == null || userId.equals("") || serverId == 0) {
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "参数错误");
            return jsonObjectReturn.toString();
        }
        Server server = serverService.get(Integer.valueOf(serverId));
        if(server == null) {
            logger.error("根据服务器Id查询服务器信息失败，serverId:" + serverId);
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "根据服务器Id查询服务器信息失败");
            return jsonObjectReturn.toString();
        }
        UserInfo userInfoCondition = new UserInfo();
        userInfoCondition.setUserId(userId);
        userInfoCondition.setServerId(server.getId());
        UserInfo userInfoDB = userInfoService.getUserInfoByServerIdAndUserId(userInfoCondition);
        if(userInfoDB == null) {
            logger.error("根据用户Id、服务器Id查询用户信息失败，userId:" + userInfo.getUserId() + "serverId:" + userInfo.getServerId());
            logger.error("根据服务器Id查询服务器信息失败，serverId:" + serverId);
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "根据用户Id、服务器Id查询用户信息失败");
            return jsonObjectReturn.toString();
        }
        String accessToken = WxUtil.getAccessToken();
        if(accessToken == null) {
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "getToken请求微信服务器失败");
            return jsonObjectReturn.toString();
        }
        String createTempTicketUrl = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=%s";
        createTempTicketUrl = String.format(createTempTicketUrl, accessToken);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("expire_seconds", 604800);
        jsonObject.put("action_name", "QR_STR_SCENE");
        JSONObject jsonObjectScene = new JSONObject();
        JSONObject jsonObjectSceneId = new JSONObject();
        String scene_str = serverId + "_" + userId;
        jsonObjectSceneId.put("scene_str", scene_str);
        jsonObjectScene.put("scene", jsonObjectSceneId);
        jsonObject.put("action_info", jsonObjectScene);
        logger.info("jsonObject:" + jsonObject.toString());
        JSONObject result = WxUtil.postToWxServer(createTempTicketUrl, jsonObject.toString());
        String errcode = result.getString("errcode");

        if(errcode == null) {
            String ticket = result.getString("ticket");
            String expire_seconds = result.getString("expire_seconds");
            String url = result.getString("url");
            logger.info("ticket:" + ticket);
            logger.info("expire_seconds:" + expire_seconds);
            logger.info("url:" + url);
            // 根据ticket换取二维码
            try {
                ticket = URLEncoder.encode(ticket, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String getQRcodeUrl = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=%s";
            getQRcodeUrl = String.format(getQRcodeUrl, ticket);
            logger.info("getQRcodeUrl:" + getQRcodeUrl);
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, true);
            jsonObjectReturn.put(Constant.data, getQRcodeUrl);
            jsonObjectReturn.put(Constant.msg, "操作成功");
            return jsonObjectReturn.toString();
        } else {
            logger.info("errcode：" + errcode);
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "向微信服务器发送Post请求出错");
            return jsonObjectReturn.toString();
        }
    }

    @GetMapping("/userClickWxMessage/{messageId}")
    public ModelAndView userClickWxMessage(@PathVariable int messageId) {
        logger.info("messageId=" + messageId);
        Message message = messageService.get(messageId);
        if(message == null) {
            logger.error("根据消息Id查找消息失败， messageId = " + messageId);
        }
        Message messageArgs = new Message();
        messageArgs.setId(message.getId());
        messageArgs.setClickTime(new Date());
        int result = messageService.updateClickTime(messageArgs);
        if(result == 1) {
            logger.info("更新消息点击时间成功");
        } else {
            logger.error("更新消息点击时间失败");
        }
        return new ModelAndView("clickMessage");
    }

    @GetMapping("/get/{id}")
    public String get(@PathVariable("id") int id) throws Exception {
        UserInfo userInfo = userInfoService.get(id);
        logger.info(userInfo);
        JSONObject jsonObjectReturn = new JSONObject();
        if(userInfo != null) {
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, true);
            jsonObjectReturn.put(Constant.data, userInfo);
            jsonObjectReturn.put(Constant.msg, "操作成功");
            return jsonObjectReturn.toString();
        } else {
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "查询用户信息失败");
            return jsonObjectReturn.toString();
        }
    }

    @GetMapping("/sendWxMsgToUser/{userId}/{templeMsgName}")
    public String sendWxMsgToUser(@PathVariable("userId") int userId, @PathVariable("templeMsgName") String templeMsgName) {
        logger.info("userId = " + userId + ", templeMsgName:" + templeMsgName);

        JSONObject jsonObjectReturn = new JSONObject();
        if(userId <= 0 || templeMsgName == null || templeMsgName.equals("")) {
            logger.error("根据Id查询用户信息失败");
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "参数错误");
            return jsonObjectReturn.toString();
        }
        UserInfo userInfo = userInfoService.get(userId);
        if(userInfo == null) {
            logger.error("根据Id查询用户信息失败");
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "查询用户信息失败");
            return jsonObjectReturn.toString();
        }
        if(templeMsgName.equals(MessageType.enumMessageType.EMT_ProcessToApprove.getName()) || templeMsgName.equals(MessageType.enumMessageType.EMT_RemoteLogin.getName())) {
            // 发送消息前，先插入message表，这样才能得到消息Id，放到推送消息链接参数里
            Message message = new Message();
            message.setSendTime(new Date());
            message.setUserId(userId);
//            message.setMsgTypeId(id);
            // 默认失败，成功后再更新为0
            message.setStatus(1);
            int result = messageService.add(message);
            logger.info("result = " +result);
            if(result == 0) {
                logger.error("添加消息失败");
                jsonObjectReturn.put(Constant.code, 200);
                jsonObjectReturn.put(Constant.success, false);
                jsonObjectReturn.put(Constant.data, null);
                jsonObjectReturn.put(Constant.msg, "添加消息失败");
                return jsonObjectReturn.toString();
            }
            boolean booleanSuccess = false;
            if(templeMsgName.equals(MessageType.enumMessageType.EMT_ProcessToApprove.getName())) {
                booleanSuccess = WxUtil.sendProcessApprovalMsgToUser(userInfo.getOpenId(), message.getId());
            }
            if(templeMsgName.equals(MessageType.enumMessageType.EMT_RemoteLogin.getName())) {
                booleanSuccess = WxUtil.sendRemoteLoginMsg(userInfo.getOpenId(), message.getId());
            }
            if(booleanSuccess) {
                result = messageService.updateStatus(message);
                if(result == 0) {
                    logger.error("微信消息已发送成功，更新发送状态失败");
                }
                jsonObjectReturn.put(Constant.code, 200);
                jsonObjectReturn.put(Constant.success, true);
                jsonObjectReturn.put(Constant.data, null);
                jsonObjectReturn.put(Constant.msg, "发送微信消息成功");
                return jsonObjectReturn.toString();
            } else {
                jsonObjectReturn.put(Constant.code, 200);
                jsonObjectReturn.put(Constant.success, false);
                jsonObjectReturn.put(Constant.data, null);
                jsonObjectReturn.put(Constant.msg, "发送微信消息失败");
                return jsonObjectReturn.toString();
            }
        }
        else {
            logger.error("没找到该消息模板，请检查是否已申请并添加到enum枚举类");
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "未查到该消息模板");
            return jsonObjectReturn.toString();
        }
    }
}
