package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.config.ScheduleConfig;
import com.example.demo.entity.*;
import com.example.demo.service.*;
import com.example.demo.util.Constant;
import com.example.demo.util.OkHttpUtil;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private BindApplyService bindApplyService;

    @Autowired
    private MessageTypeService messageTypeService;

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
//        System.out.println("hs:" + hs);

        PageInfo<UserInfo> page = new PageInfo<>(hs,5);

        return page;
    }

    @ResponseBody
    @PostMapping("/getQRcodeByServerIpAndUserId")
    public String getQRcodeByServerIpAndUserId(@RequestBody UserInfo userInfo) {
        logger.info("getQRcode");
        logger.info(userInfo);
        String serverIp = userInfo.getServerIp();
        String userId = userInfo.getUserId();
        JSONObject jsonObjectReturn = new JSONObject();
        if(userId == null || userId.equals("") || serverIp == null || serverIp.equals("")) {
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "参数错误");
            return jsonObjectReturn.toString();
        }
//        Server server = serverService.get(Integer.valueOf(serverId));
        Server server = serverService.getServerByServerIp(serverIp);
        if(server == null) {
            logger.error("根据服务器serverIp查询服务器信息失败，serverIp = " + serverIp);
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
        String scene_str = serverIp + "_" + userId;
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

    @ResponseBody
    @PostMapping("/getQRcode")
    public String getQRcode(@RequestBody UserInfo userInfo) {
        logger.info("getQRcode, " + userInfo);
        logger.info(userInfo);
        String serverIp = userInfo.getServerIp();
        String userId = userInfo.getUserId();
        String sign = userInfo.getSign();
        //
        JSONObject jsonObjectReturn = new JSONObject();
        if(userId == null || userId.equals("") || userId.length() < 2 || serverIp == null || serverIp.equals("") || serverIp.length() < 2) {
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "参数错误");
            return jsonObjectReturn.toString();
        }
        String ipTail2 = serverIp.substring(serverIp.length() - 2);
        String userIdHead2 = userId.substring(0, 2);
        userIdHead2 = userIdHead2.toUpperCase();
        logger.info("ipTail2 = " + ipTail2 + ",userIdHead2 = " + userIdHead2);
        String mergeServerIdAndUserId = ipTail2 + userIdHead2;
        String mergeServerIdAndUserIdEncode = WxUtil.signEnCode(mergeServerIdAndUserId);
        sign = sign.toUpperCase();
        mergeServerIdAndUserIdEncode = mergeServerIdAndUserIdEncode.toUpperCase();
        logger.info("mergeServerIdAndUserIdEncode = " + mergeServerIdAndUserIdEncode + ", sign = " + sign);
        if(sign == null || !sign.equals(mergeServerIdAndUserIdEncode)) {
            logger.error("无权限，sign = " + sign +",signCheck = " + mergeServerIdAndUserIdEncode);
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "无权限");
            return jsonObjectReturn.toString();
        }
        Server server = serverService.getServerByServerIp(serverIp);
        if(server == null) {
            logger.error("根据服务器serverIp查询服务器信息失败，serverIp = " + serverIp);
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
              // 目前放开限制用户必须在服务器上登记过，后面审核可以新增该用户
//            jsonObjectReturn.put(Constant.code, 200);
//            jsonObjectReturn.put(Constant.success, false);
//            jsonObjectReturn.put(Constant.data, null);
//            jsonObjectReturn.put(Constant.msg, "根据用户Id、服务器Id查询用户信息失败");
//            return jsonObjectReturn.toString();
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
        String scene_str = serverIp + "_" + userId;
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
            //
            byte[] data = WxUtil.imageToByte(getQRcodeUrl);
            if(data != null) {
                jsonObjectReturn.put(Constant.code, 200);
                jsonObjectReturn.put(Constant.success, true);
                jsonObjectReturn.put(Constant.data, data);
                jsonObjectReturn.put(Constant.msg, "操作成功");
                return jsonObjectReturn.toString();
            } else {
                logger.error("图片转二进制流失败");
                jsonObjectReturn.put(Constant.code, 200);
                jsonObjectReturn.put(Constant.success, false);
                jsonObjectReturn.put(Constant.data, null);
                jsonObjectReturn.put(Constant.msg, "图片转二进制流失败");
                return jsonObjectReturn.toString();
            }
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

    @GetMapping("/getMessageInfo/{messageId}/{userCode}")
    public String getMessageInfo(@PathVariable int messageId, @PathVariable String userCode) {
        logger.info("messageId=" + messageId + ",userCode=" + userCode);
        JSONObject jsonObjectReturn = new JSONObject();
        Message message = messageService.get(messageId);
        if(message == null) {
            logger.error("根据消息Id查找消息失败， messageId = " + messageId);
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "查找消息失败");
            return jsonObjectReturn.toString();
        }
        Message messageArgs = new Message();
        messageArgs.setId(message.getId());
        messageArgs.setClickTime(new Date());
        int result = messageService.updateClickTime(messageArgs);
        if(result == 1) {
            logger.info("更新消息点击时间成功");
        } else {
            logger.error("更新消息点击时间失败");
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "更新消息点击时间失败");
            return jsonObjectReturn.toString();
        }
        if(message.getMsgTypeId() != 0) {
            MessageType messageType = messageTypeService.get(message.getMsgTypeId());
            // todo 判空
            if(messageType == null) {

            }
            if(messageType.getMessageName().equals(MessageType.enumMessageType.EMT_ProcessToApprove.getName())) {
                if(messageType.getMessageTime() != null) {
                    Server server = serverService.get(messageType.getServerId());
                    if(server == null || server.getServerUrl() == null) {
                        logger.error("找不到服务器信息或服务器 serverUrl 为空，id=" + messageType.getServerId());
                        jsonObjectReturn.put(Constant.code, 200);
                        jsonObjectReturn.put(Constant.success, false);
                        jsonObjectReturn.put(Constant.data, null);
                        jsonObjectReturn.put(Constant.msg, "参数错误");
                        return jsonObjectReturn.toString();
                    }
                    if (messageType.getMessageTime().equals("早上")) {
                        Map<String, String> hashMap = new HashMap<>();
                        hashMap.put("userCode", userCode);
                        List<PendingApprovalDetail> pendingApprovalDetail = OkHttpUtil.getPendingApprovalDetailList(server.getServerUrl(), hashMap);
                        if(pendingApprovalDetail == null) {
                            logger.error("查询待审批详情失败");
                            jsonObjectReturn.put(Constant.code, 200);
                            jsonObjectReturn.put(Constant.success, false);
                            jsonObjectReturn.put(Constant.data, null);
                            jsonObjectReturn.put(Constant.msg, "查询待审批详情失败");
                            return jsonObjectReturn.toString();
                        }
                        jsonObjectReturn.put(Constant.code, 200);
                        jsonObjectReturn.put(Constant.success, true);
                        jsonObjectReturn.put(Constant.data, pendingApprovalDetail);
                        jsonObjectReturn.put(Constant.msg, "操作成功");
                        return jsonObjectReturn.toString();
                    } else if (messageType.getMessageTime().equals("晚上")) {
                        Map<String, String> hashMap = new HashMap<>();
                        hashMap.put("userCode", userCode);
                        // 用户今日已审、待审数目
                        PendingApproval pendingApprovalAdTotalCount = OkHttpUtil.getAdTototalCountByUserCode(server.getServerUrl(), hashMap);
                        if(pendingApprovalAdTotalCount == null) {
                            logger.error("查询用户" + userCode + "今日已审、待审数目");
//                            jsonObjectReturn.put(Constant.code, 200);
//                            jsonObjectReturn.put(Constant.success, false);
//                            jsonObjectReturn.put(Constant.data, null);
//                            jsonObjectReturn.put(Constant.msg, "查询用户" + userCode + "今日已审、待审数目失败");
//                            return jsonObjectReturn.toString();
                        }
                        // 用户平均时效
                        PendingApproval pendingApprovalAverageTime = OkHttpUtil.getAverageTimeByUserCode(server.getServerUrl(), hashMap);
                        if(pendingApprovalAverageTime == null) {
                            logger.error("查询用户" + userCode + "平均时效失败");
//                            jsonObjectReturn.put(Constant.code, 200);
//                            jsonObjectReturn.put(Constant.success, false);
//                            jsonObjectReturn.put(Constant.data, null);
//                            jsonObjectReturn.put(Constant.msg, "查询用户" + userCode + "平均时效失败");
//                            return jsonObjectReturn.toString();
                        }
                        // 今日已审排行
                        List<PendingApproval> pendingApprovalTotalApprovalRank = OkHttpUtil.getTotalApprovalRank(server.getServerUrl(), null);
                        if(pendingApprovalTotalApprovalRank == null) {
                            logger.error("查询今日审核数目排行失败");
//                            jsonObjectReturn.put(Constant.code, 200);
//                            jsonObjectReturn.put(Constant.success, false);
//                            jsonObjectReturn.put(Constant.data, null);
//                            jsonObjectReturn.put(Constant.msg, "查询今日审核数目排行失败");
//                            return jsonObjectReturn.toString();
                        }
                        JSONObject jsonObject = new JSONObject();
                        // 用户今日已审、待审数目
                        jsonObject.put("pendingApprovalAdTotalCount", pendingApprovalAdTotalCount);
                        // 用户平均时效
                        jsonObject.put("pendingApprovalAverageTime", pendingApprovalAverageTime);
                        // 今日已审排行
                        jsonObject.put("pendingApprovalTotalApprovalRank", pendingApprovalTotalApprovalRank);
                        jsonObjectReturn.put(Constant.code, 200);
                        jsonObjectReturn.put(Constant.success, true);
                        jsonObjectReturn.put(Constant.data, jsonObject);
                        jsonObjectReturn.put(Constant.msg, "操作成功");
                        return jsonObjectReturn.toString();
                    } else {
                        logger.info("未处理的消息时间，messageTime=" + messageType.getMessageTime());
                        jsonObjectReturn.put(Constant.code, 200);
                        jsonObjectReturn.put(Constant.success, false);
                        jsonObjectReturn.put(Constant.data, null);
                        jsonObjectReturn.put(Constant.msg, "未处理的消息时间，messageTime=" + messageType.getMessageTime());
                        return jsonObjectReturn.toString();
                    }
                } else {
                    logger.error("消息类型的messageTime为null");
                    jsonObjectReturn.put(Constant.code, 200);
                    jsonObjectReturn.put(Constant.success, false);
                    jsonObjectReturn.put(Constant.data, null);
                    jsonObjectReturn.put(Constant.msg, "服务器错误");
                    return jsonObjectReturn.toString();
                }
            }
        } else {
            logger.info("未处理的消息类型Id=" + message.getMsgTypeId());
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "未处理的消息类型Id=" + message.getMsgTypeId());
            return jsonObjectReturn.toString();
        }
        return jsonObjectReturn.toString();
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

    // 给单个用户发送的sql还没有
//    @GetMapping("/sendWxMsgToUser/{userId}/{templeMsgName}")
//    public String sendWxMsgToUser(@PathVariable("userId") int userId, @PathVariable("templeMsgName") String templeMsgName) {
//        logger.info("userId = " + userId + ", templeMsgName:" + templeMsgName);
//
//        JSONObject jsonObjectReturn = new JSONObject();
//        if(userId <= 0 || templeMsgName == null || templeMsgName.equals("")) {
//            logger.error("根据Id查询用户信息失败");
//            jsonObjectReturn.put(Constant.code, 200);
//            jsonObjectReturn.put(Constant.success, false);
//            jsonObjectReturn.put(Constant.data, null);
//            jsonObjectReturn.put(Constant.msg, "参数错误");
//            return jsonObjectReturn.toString();
//        }
//        UserInfo userInfo = userInfoService.get(userId);
//        if(userInfo == null) {
//            logger.error("根据Id查询用户信息失败");
//            jsonObjectReturn.put(Constant.code, 200);
//            jsonObjectReturn.put(Constant.success, false);
//            jsonObjectReturn.put(Constant.data, null);
//            jsonObjectReturn.put(Constant.msg, "查询用户信息失败");
//            return jsonObjectReturn.toString();
//        }
//        if(templeMsgName.equals(MessageType.enumMessageType.EMT_ProcessToApprove.getName()) || templeMsgName.equals(MessageType.enumMessageType.EMT_RemoteLogin.getName())) {
//            // 发送消息前，先插入message表，这样才能得到消息Id，放到推送消息链接参数里
//            Message message = new Message();
//            message.setSendTime(new Date());
//            message.setUserId(userId);
////            message.setMsgTypeId(id);
//            // 默认失败，成功后再更新为0
//            message.setStatus(1);
//            int result = messageService.add(message);
//            logger.info("result = " +result);
//            if(result == 0) {
//                logger.error("添加Server消息失败");
//                jsonObjectReturn.put(Constant.code, 200);
//                jsonObjectReturn.put(Constant.success, false);
//                jsonObjectReturn.put(Constant.data, null);
//                jsonObjectReturn.put(Constant.msg, "添加消息失败");
//                return jsonObjectReturn.toString();
//            }
//            boolean booleanSuccess = false;
//            if(templeMsgName.equals(MessageType.enumMessageType.EMT_ProcessToApprove.getName())) {
//                int serverId = userInfo.getServerId();
//                Server server = serverService.get(serverId);
//                if(server != null && server.getServerUrl() != null && !server.equals("")) {
//                    List<PendingApproval> pendingApprovalList = OkHttpUtil.getPendingApprovalList(server.getServerUrl());
//                    if(pendingApprovalList != null) {
//                        booleanSuccess = WxUtil.sendProcessApprovalMsgToUser(userInfo.getOpenId(), message.getId(), pendingApprovalList.size());
//                    } else {
//                        logger.error("pendingApprovalList 为 null");
//                    }
//                } else {
//                    logger.error("服务器或服务器url为null");
//                }
//            }
//            if(templeMsgName.equals(MessageType.enumMessageType.EMT_RemoteLogin.getName())) {
//                booleanSuccess = WxUtil.sendRemoteLoginMsg(userInfo.getOpenId(), message.getId());
//            }
//            if(booleanSuccess) {
//                result = messageService.updateStatus(message);
//                if(result == 0) {
//                    logger.error("微信消息已发送成功，更新发送状态失败");
//                }
//                jsonObjectReturn.put(Constant.code, 200);
//                jsonObjectReturn.put(Constant.success, true);
//                jsonObjectReturn.put(Constant.data, null);
//                jsonObjectReturn.put(Constant.msg, "发送微信消息成功");
//                return jsonObjectReturn.toString();
//            } else {
//                jsonObjectReturn.put(Constant.code, 200);
//                jsonObjectReturn.put(Constant.success, false);
//                jsonObjectReturn.put(Constant.data, null);
//                jsonObjectReturn.put(Constant.msg, "发送微信消息失败");
//                return jsonObjectReturn.toString();
//            }
//        }
//        else {
//            logger.error("没找到该消息模板，请检查是否已申请并添加到enum枚举类");
//            jsonObjectReturn.put(Constant.code, 200);
//            jsonObjectReturn.put(Constant.success, false);
//            jsonObjectReturn.put(Constant.data, null);
//            jsonObjectReturn.put(Constant.msg, "未查到该消息模板");
//            return jsonObjectReturn.toString();
//        }
//    }

    @PostMapping("/unBind")
    public String unBind(@RequestBody UserInfo userInfo) {
        JSONObject jsonObjectReturn = new JSONObject();
        if(userInfo.getId() <= 0) {
            logger.error("id参数不合法");
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "请检查id参数");
            return jsonObjectReturn.toString();
        }
        UserInfo userInfoDB = userInfoService.get(userInfo.getId());
        if(userInfoDB == null) {
            logger.error("根据用户Id查找用户信息失败：id = " + userInfo.getId());
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "操作失败");
            return jsonObjectReturn.toString();
        }
        UserInfo userInfoArgs = new UserInfo();
        userInfoArgs.setId(userInfo.getId());
        userInfoArgs.setWxNickname(null);
        userInfoArgs.setOpenId(null);
        int result = userInfoService.updateOpenIdAndNickName(userInfoArgs);
        if(result == 1) {
            // 删除对应账号的绑定申请信息,设置status为已删除状态，userId设为null
            BindApply bindApplyArgs = new BindApply();
            bindApplyArgs.setServerId(userInfoDB.getServerId());
            bindApplyArgs.setUserCode(userInfoDB.getUserId());
            result = bindApplyService.deleteByServerIdAndUserCode(bindApplyArgs);
            // 可能删除多条，result > 1
            if(result == 0) {
                logger.error("删除serverId=" + userInfoDB.getServerId() +",userId=" + userInfoDB.getUserId() + "的绑定申请信息失败");
                jsonObjectReturn.put(Constant.code, 200);
                jsonObjectReturn.put(Constant.success, false);
                jsonObjectReturn.put(Constant.data, null);
                jsonObjectReturn.put(Constant.msg, "操作失败");
                return jsonObjectReturn.toString();
            } else {
                jsonObjectReturn.put(Constant.code, 200);
                jsonObjectReturn.put(Constant.success, true);
                jsonObjectReturn.put(Constant.data, null);
                jsonObjectReturn.put(Constant.msg, "操作成功");
                return jsonObjectReturn.toString();
            }
        } else {
            logger.error("更新微信昵称和openId失败");
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "服务器错误");
            return jsonObjectReturn.toString();
        }
    }
}
