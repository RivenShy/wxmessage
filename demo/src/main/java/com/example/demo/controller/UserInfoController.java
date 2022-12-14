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
            jsonObjectReturn.put(Constant.msg, "????????????");
            return jsonObjectReturn.toString();
        }
//        Server server = serverService.get(Integer.valueOf(serverId));
        Server server = serverService.getServerByServerIp(serverIp);
        if(server == null) {
            logger.error("???????????????serverIp??????????????????????????????serverIp = " + serverIp);
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "???????????????Id???????????????????????????");
            return jsonObjectReturn.toString();
        }
        UserInfo userInfoCondition = new UserInfo();
        userInfoCondition.setUserId(userId);
        userInfoCondition.setServerId(server.getId());
        UserInfo userInfoDB = userInfoService.getUserInfoByServerIdAndUserId(userInfoCondition);
        if(userInfoDB == null) {
            logger.error("????????????Id????????????Id???????????????????????????userId:" + userInfo.getUserId() + "serverId:" + userInfo.getServerId());
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "????????????Id????????????Id????????????????????????");
            return jsonObjectReturn.toString();
        }
        String accessToken = WxUtil.getAccessToken();
        if(accessToken == null) {
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "getToken???????????????????????????");
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
            // ??????ticket???????????????
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
            jsonObjectReturn.put(Constant.msg, "????????????");
            return jsonObjectReturn.toString();
        } else {
            logger.info("errcode???" + errcode);
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "????????????????????????Post????????????");
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
            jsonObjectReturn.put(Constant.msg, "????????????");
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
            logger.error("????????????sign = " + sign +",signCheck = " + mergeServerIdAndUserIdEncode);
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "?????????");
            return jsonObjectReturn.toString();
        }
        Server server = serverService.getServerByServerIp(serverIp);
        if(server == null) {
            logger.error("???????????????serverIp??????????????????????????????serverIp = " + serverIp);
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "???????????????Id???????????????????????????");
            return jsonObjectReturn.toString();
        }
        UserInfo userInfoCondition = new UserInfo();
        userInfoCondition.setUserId(userId);
        userInfoCondition.setServerId(server.getId());
        UserInfo userInfoDB = userInfoService.getUserInfoByServerIdAndUserId(userInfoCondition);
        if(userInfoDB == null) {
            logger.error("????????????Id????????????Id???????????????????????????userId:" + userInfo.getUserId() + "serverId:" + userInfo.getServerId());
              // ??????????????????????????????????????????????????????????????????????????????????????????
//            jsonObjectReturn.put(Constant.code, 200);
//            jsonObjectReturn.put(Constant.success, false);
//            jsonObjectReturn.put(Constant.data, null);
//            jsonObjectReturn.put(Constant.msg, "????????????Id????????????Id????????????????????????");
//            return jsonObjectReturn.toString();
        }
        String accessToken = WxUtil.getAccessToken();
        if(accessToken == null) {
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "getToken???????????????????????????");
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
            // ??????ticket???????????????
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
                jsonObjectReturn.put(Constant.msg, "????????????");
                return jsonObjectReturn.toString();
            } else {
                logger.error("???????????????????????????");
                jsonObjectReturn.put(Constant.code, 200);
                jsonObjectReturn.put(Constant.success, false);
                jsonObjectReturn.put(Constant.data, null);
                jsonObjectReturn.put(Constant.msg, "???????????????????????????");
                return jsonObjectReturn.toString();
            }
        } else {
            logger.info("errcode???" + errcode);
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "????????????????????????Post????????????");
            return jsonObjectReturn.toString();
        }
    }

    @GetMapping("/userClickWxMessage/{messageId}")
    public ModelAndView userClickWxMessage(@PathVariable int messageId) {
        logger.info("messageId=" + messageId);
        Message message = messageService.get(messageId);
        if(message == null) {
            logger.error("????????????Id????????????????????? messageId = " + messageId);
        }
        Message messageArgs = new Message();
        messageArgs.setId(message.getId());
        messageArgs.setClickTime(new Date());
        int result = messageService.updateClickTime(messageArgs);
        if(result == 1) {
            logger.info("??????????????????????????????");
        } else {
            logger.error("??????????????????????????????");
        }
        return new ModelAndView("clickMessage");
    }

    @GetMapping("/getMessageInfo/{messageId}/{userCode}")
    public String getMessageInfo(@PathVariable int messageId, @PathVariable String userCode) {
        logger.info("messageId=" + messageId + ",userCode=" + userCode);
        JSONObject jsonObjectReturn = new JSONObject();
        Message message = messageService.get(messageId);
        if(message == null) {
            logger.error("????????????Id????????????????????? messageId = " + messageId);
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "??????????????????");
            return jsonObjectReturn.toString();
        }
        Message messageArgs = new Message();
        messageArgs.setId(message.getId());
        messageArgs.setClickTime(new Date());
        int result = messageService.updateClickTime(messageArgs);
        if(result == 1) {
            logger.info("??????????????????????????????");
        } else {
            logger.error("??????????????????????????????");
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "??????????????????????????????");
            return jsonObjectReturn.toString();
        }
        if(message.getMsgTypeId() != 0) {
            MessageType messageType = messageTypeService.get(message.getMsgTypeId());
            // todo ??????
            if(messageType == null) {

            }
            if(messageType.getMessageName().equals(MessageType.enumMessageType.EMT_ProcessToApprove.getName())) {
                if(messageType.getMessageTime() != null) {
                    Server server = serverService.get(messageType.getServerId());
                    if(server == null || server.getServerUrl() == null) {
                        logger.error("???????????????????????????????????? serverUrl ?????????id=" + messageType.getServerId());
                        jsonObjectReturn.put(Constant.code, 200);
                        jsonObjectReturn.put(Constant.success, false);
                        jsonObjectReturn.put(Constant.data, null);
                        jsonObjectReturn.put(Constant.msg, "????????????");
                        return jsonObjectReturn.toString();
                    }
                    if (messageType.getMessageTime().equals("??????")) {
                        Map<String, String> hashMap = new HashMap<>();
                        hashMap.put("userCode", userCode);
                        List<PendingApprovalDetail> pendingApprovalDetail = OkHttpUtil.getPendingApprovalDetailList(server.getServerUrl(), hashMap);
                        if(pendingApprovalDetail == null) {
                            logger.error("???????????????????????????");
                            jsonObjectReturn.put(Constant.code, 200);
                            jsonObjectReturn.put(Constant.success, false);
                            jsonObjectReturn.put(Constant.data, null);
                            jsonObjectReturn.put(Constant.msg, "???????????????????????????");
                            return jsonObjectReturn.toString();
                        }
                        jsonObjectReturn.put(Constant.code, 200);
                        jsonObjectReturn.put(Constant.success, true);
                        jsonObjectReturn.put(Constant.data, pendingApprovalDetail);
                        jsonObjectReturn.put(Constant.msg, "????????????");
                        return jsonObjectReturn.toString();
                    } else if (messageType.getMessageTime().equals("??????")) {
                        Map<String, String> hashMap = new HashMap<>();
                        hashMap.put("userCode", userCode);
                        // ?????????????????????????????????
                        PendingApproval pendingApprovalAdTotalCount = OkHttpUtil.getAdTototalCountByUserCode(server.getServerUrl(), hashMap);
                        if(pendingApprovalAdTotalCount == null) {
                            logger.error("????????????" + userCode + "???????????????????????????");
//                            jsonObjectReturn.put(Constant.code, 200);
//                            jsonObjectReturn.put(Constant.success, false);
//                            jsonObjectReturn.put(Constant.data, null);
//                            jsonObjectReturn.put(Constant.msg, "????????????" + userCode + "?????????????????????????????????");
//                            return jsonObjectReturn.toString();
                        }
                        // ??????????????????
                        PendingApproval pendingApprovalAverageTime = OkHttpUtil.getAverageTimeByUserCode(server.getServerUrl(), hashMap);
                        if(pendingApprovalAverageTime == null) {
                            logger.error("????????????" + userCode + "??????????????????");
//                            jsonObjectReturn.put(Constant.code, 200);
//                            jsonObjectReturn.put(Constant.success, false);
//                            jsonObjectReturn.put(Constant.data, null);
//                            jsonObjectReturn.put(Constant.msg, "????????????" + userCode + "??????????????????");
//                            return jsonObjectReturn.toString();
                        }
                        // ??????????????????
                        List<PendingApproval> pendingApprovalTotalApprovalRank = OkHttpUtil.getTotalApprovalRank(server.getServerUrl(), null);
                        if(pendingApprovalTotalApprovalRank == null) {
                            logger.error("????????????????????????????????????");
//                            jsonObjectReturn.put(Constant.code, 200);
//                            jsonObjectReturn.put(Constant.success, false);
//                            jsonObjectReturn.put(Constant.data, null);
//                            jsonObjectReturn.put(Constant.msg, "????????????????????????????????????");
//                            return jsonObjectReturn.toString();
                        }
                        JSONObject jsonObject = new JSONObject();
                        // ?????????????????????????????????
                        jsonObject.put("pendingApprovalAdTotalCount", pendingApprovalAdTotalCount);
                        // ??????????????????
                        jsonObject.put("pendingApprovalAverageTime", pendingApprovalAverageTime);
                        // ??????????????????
                        jsonObject.put("pendingApprovalTotalApprovalRank", pendingApprovalTotalApprovalRank);
                        jsonObjectReturn.put(Constant.code, 200);
                        jsonObjectReturn.put(Constant.success, true);
                        jsonObjectReturn.put(Constant.data, jsonObject);
                        jsonObjectReturn.put(Constant.msg, "????????????");
                        return jsonObjectReturn.toString();
                    } else {
                        logger.info("???????????????????????????messageTime=" + messageType.getMessageTime());
                        jsonObjectReturn.put(Constant.code, 200);
                        jsonObjectReturn.put(Constant.success, false);
                        jsonObjectReturn.put(Constant.data, null);
                        jsonObjectReturn.put(Constant.msg, "???????????????????????????messageTime=" + messageType.getMessageTime());
                        return jsonObjectReturn.toString();
                    }
                } else {
                    logger.error("???????????????messageTime???null");
                    jsonObjectReturn.put(Constant.code, 200);
                    jsonObjectReturn.put(Constant.success, false);
                    jsonObjectReturn.put(Constant.data, null);
                    jsonObjectReturn.put(Constant.msg, "???????????????");
                    return jsonObjectReturn.toString();
                }
            }
        } else {
            logger.info("????????????????????????Id=" + message.getMsgTypeId());
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "????????????????????????Id=" + message.getMsgTypeId());
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
            jsonObjectReturn.put(Constant.msg, "????????????");
            return jsonObjectReturn.toString();
        } else {
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "????????????????????????");
            return jsonObjectReturn.toString();
        }
    }

    // ????????????????????????sql?????????
//    @GetMapping("/sendWxMsgToUser/{userId}/{templeMsgName}")
//    public String sendWxMsgToUser(@PathVariable("userId") int userId, @PathVariable("templeMsgName") String templeMsgName) {
//        logger.info("userId = " + userId + ", templeMsgName:" + templeMsgName);
//
//        JSONObject jsonObjectReturn = new JSONObject();
//        if(userId <= 0 || templeMsgName == null || templeMsgName.equals("")) {
//            logger.error("??????Id????????????????????????");
//            jsonObjectReturn.put(Constant.code, 200);
//            jsonObjectReturn.put(Constant.success, false);
//            jsonObjectReturn.put(Constant.data, null);
//            jsonObjectReturn.put(Constant.msg, "????????????");
//            return jsonObjectReturn.toString();
//        }
//        UserInfo userInfo = userInfoService.get(userId);
//        if(userInfo == null) {
//            logger.error("??????Id????????????????????????");
//            jsonObjectReturn.put(Constant.code, 200);
//            jsonObjectReturn.put(Constant.success, false);
//            jsonObjectReturn.put(Constant.data, null);
//            jsonObjectReturn.put(Constant.msg, "????????????????????????");
//            return jsonObjectReturn.toString();
//        }
//        if(templeMsgName.equals(MessageType.enumMessageType.EMT_ProcessToApprove.getName()) || templeMsgName.equals(MessageType.enumMessageType.EMT_RemoteLogin.getName())) {
//            // ???????????????????????????message??????????????????????????????Id????????????????????????????????????
//            Message message = new Message();
//            message.setSendTime(new Date());
//            message.setUserId(userId);
////            message.setMsgTypeId(id);
//            // ????????????????????????????????????0
//            message.setStatus(1);
//            int result = messageService.add(message);
//            logger.info("result = " +result);
//            if(result == 0) {
//                logger.error("??????Server????????????");
//                jsonObjectReturn.put(Constant.code, 200);
//                jsonObjectReturn.put(Constant.success, false);
//                jsonObjectReturn.put(Constant.data, null);
//                jsonObjectReturn.put(Constant.msg, "??????????????????");
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
//                        logger.error("pendingApprovalList ??? null");
//                    }
//                } else {
//                    logger.error("?????????????????????url???null");
//                }
//            }
//            if(templeMsgName.equals(MessageType.enumMessageType.EMT_RemoteLogin.getName())) {
//                booleanSuccess = WxUtil.sendRemoteLoginMsg(userInfo.getOpenId(), message.getId());
//            }
//            if(booleanSuccess) {
//                result = messageService.updateStatus(message);
//                if(result == 0) {
//                    logger.error("??????????????????????????????????????????????????????");
//                }
//                jsonObjectReturn.put(Constant.code, 200);
//                jsonObjectReturn.put(Constant.success, true);
//                jsonObjectReturn.put(Constant.data, null);
//                jsonObjectReturn.put(Constant.msg, "????????????????????????");
//                return jsonObjectReturn.toString();
//            } else {
//                jsonObjectReturn.put(Constant.code, 200);
//                jsonObjectReturn.put(Constant.success, false);
//                jsonObjectReturn.put(Constant.data, null);
//                jsonObjectReturn.put(Constant.msg, "????????????????????????");
//                return jsonObjectReturn.toString();
//            }
//        }
//        else {
//            logger.error("???????????????????????????????????????????????????????????????enum?????????");
//            jsonObjectReturn.put(Constant.code, 200);
//            jsonObjectReturn.put(Constant.success, false);
//            jsonObjectReturn.put(Constant.data, null);
//            jsonObjectReturn.put(Constant.msg, "????????????????????????");
//            return jsonObjectReturn.toString();
//        }
//    }

    @PostMapping("/unBind")
    public String unBind(@RequestBody UserInfo userInfo) {
        JSONObject jsonObjectReturn = new JSONObject();
        if(userInfo.getId() <= 0) {
            logger.error("id???????????????");
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "?????????id??????");
            return jsonObjectReturn.toString();
        }
        UserInfo userInfoDB = userInfoService.get(userInfo.getId());
        if(userInfoDB == null) {
            logger.error("????????????Id???????????????????????????id = " + userInfo.getId());
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "????????????");
            return jsonObjectReturn.toString();
        }
        UserInfo userInfoArgs = new UserInfo();
        userInfoArgs.setId(userInfo.getId());
        userInfoArgs.setWxNickname(null);
        userInfoArgs.setOpenId(null);
        int result = userInfoService.updateOpenIdAndNickName(userInfoArgs);
        if(result == 1) {
            // ???????????????????????????????????????,??????status?????????????????????userId??????null
            BindApply bindApplyArgs = new BindApply();
            bindApplyArgs.setServerId(userInfoDB.getServerId());
            bindApplyArgs.setUserCode(userInfoDB.getUserId());
            result = bindApplyService.deleteByServerIdAndUserCode(bindApplyArgs);
            // ?????????????????????result > 1
            if(result == 0) {
                logger.error("??????serverId=" + userInfoDB.getServerId() +",userId=" + userInfoDB.getUserId() + "???????????????????????????");
                jsonObjectReturn.put(Constant.code, 200);
                jsonObjectReturn.put(Constant.success, false);
                jsonObjectReturn.put(Constant.data, null);
                jsonObjectReturn.put(Constant.msg, "????????????");
                return jsonObjectReturn.toString();
            } else {
                jsonObjectReturn.put(Constant.code, 200);
                jsonObjectReturn.put(Constant.success, true);
                jsonObjectReturn.put(Constant.data, null);
                jsonObjectReturn.put(Constant.msg, "????????????");
                return jsonObjectReturn.toString();
            }
        } else {
            logger.error("?????????????????????openId??????");
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "???????????????");
            return jsonObjectReturn.toString();
        }
    }
}
