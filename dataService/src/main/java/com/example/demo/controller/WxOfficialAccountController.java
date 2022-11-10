package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.BindApply;
import com.example.demo.entity.Server;
import com.example.demo.entity.UserInfo;
import com.example.demo.service.BindApplyService;
import com.example.demo.service.ServerService;
import com.example.demo.service.UserInfoService;
import com.example.demo.util.Constant;
import com.example.demo.util.WxUtil;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/wx")
public class WxOfficialAccountController {

    private static Logger logger = Logger.getLogger(WxOfficialAccountController.class);

    public static final String TOKEN = "projectManagerSystem";

    // 公众号appid、appsecret
    public static final String appid = "wx226ffc0b68fa17e9";
    public static final String appsecret = "4a4037b79e0390da5a4d8cb8ff5014f0";

    @Autowired
    BindApplyService bindApplyService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    ServerService serverService;
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
//        String[] strArray = {TOKEN, timestamp, nonce};
//        Arrays.sort(strArray);
////        2）将三个参数字符串拼接成一个字符串进行sha1加密
//        StringBuilder stringBuilder = new StringBuilder();
//        for(String str : strArray) {
//            stringBuilder.append(str);
//        }
//        String encryption = WxUtil.getSha1(stringBuilder.toString());
////        3）开发者获得加密后的字符串可与 signature 对比，标识该请求来源于微信
//        if(encryption.equals(signature)) {
//            out.print(echostr);
//            out.flush();
//            out.close();
//        }
//    }


    @PostMapping("/validate")
    public void handlerWechatEvent(HttpServletRequest request, HttpServletResponse response, @RequestBody String xmlData) throws DocumentException {
        if(xmlData != null) {
            logger.info("xmlData:" + xmlData);
            Document doc = DocumentHelper.parseText(xmlData);
            Element root = doc.getRootElement();
            String MsgType = root.elementText("MsgType");
            if(MsgType.equals("event")) {
                String Event = root.elementText("Event");
                if(Event.equals("SCAN")) {
                    String ToUserName = root.elementText("ToUserName");
                    String FromUserName = root.elementText("FromUserName");
                    String EventKey = root.elementText("EventKey");
                    logger.info("FromUserName:" + FromUserName);
                    logger.info("ToUserName:" + ToUserName);
                    logger.info("EventKey:" + EventKey);
//                    User user = userMapper.getReferenceById(Integer.valueOf(EventKey));
//                    BindApply bindApply = new BindApply();
//                    bindApply.setUserId(user.getId());
//                    bindApply.setUserOpenid(FromUserName);
                    // TODO
//                    bindApplyService.save(bindApply);
                    String serverId = EventKey.split("_")[0];
                    if(serverId == null) {
                        logger.error("serverId为null");
                        return;
                    }
                    String userId = EventKey.split("_")[1];
                    if(userId == null) {
                        logger.error("userId为null");
                        return;
                    }
                    String REDIRECT_URI = "http://www.szwd.online//wx/outhPage/" + serverId + "/" + userId;
                    String outhUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
                    outhUrl = String.format(outhUrl, WxOfficialAccountController.appid, REDIRECT_URI);
                    WxUtil.sendOuthMsgToUser(FromUserName, outhUrl);
                    // 关注事件
                } else if(Event.equals("subscribe")) {
                    String ToUserName = root.elementText("ToUserName");
                    String FromUserName = root.elementText("FromUserName");
                    String EventKey = root.elementText("EventKey");
                    logger.info("FromUserName:" + FromUserName);
                    logger.info("ToUserName:" + ToUserName);
                    logger.info("EventKey:" + EventKey);
                    // 判断是否是扫码后的关注事件
                    if(EventKey != null && !EventKey.equals("")) {
                        String serverId = EventKey.split("_")[1];
                        if(serverId == null) {
                            logger.error("serverId为null");
                            return;
                        }
                        String userId = EventKey.split("_")[2];
                        if(userId == null) {
                            logger.error("userId为null");
                            return;
                        }
                        if(userId != null && !userId.equals("")) {
                            String REDIRECT_URI = "http://www.szwd.online//wx/outhPage/" + serverId + "/" + userId;
                            String outhUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
                            outhUrl = String.format(outhUrl, WxOfficialAccountController.appid, REDIRECT_URI);
                            WxUtil.sendOuthMsgToUser(FromUserName, outhUrl);
                        }
                    }
                }
            }
        } else {
            logger.info("XML 数据包为null");
        }

    }

    public String getWxOfficialAccountAccessToken() {
//		有效期两小时
        String accessToken = null;
        String expiresIn = null;
        String getTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
        getTokenUrl = String.format(getTokenUrl, appid, appsecret);
        JSONObject jsonObject = WxUtil.getDataFromWxServer(getTokenUrl);
        String errcode = jsonObject.getString("errcode");
        if(errcode == null) {
            logger.info("getToken请求微信服务器成功");
            accessToken = jsonObject.getString("access_token");
            expiresIn = jsonObject.getString("expires_in");
            logger.info("accessToken:" + accessToken);
            logger.info("expiresIn:" + expiresIn);
            return accessToken;
        } else {
            String errmsg = jsonObject.getString("errmsg");
            logger.info("getToken请求微信服务器失败，errcode:" + errcode + ",errmsg:" + errmsg);
            return null;
        }
    }

//    @GetMapping("/bindPage")
//    public ModelAndView bind() {
//        ModelAndView  modelAndView= new ModelAndView("bind.html");
//        return modelAndView;
//    }

    @GetMapping("/bindPage")
    public String bind() {
        return "bind";
    }

    @GetMapping("/outhPage/{serverId}/{userId}")
    public ModelAndView outh(@PathVariable("serverId") String serverId, @PathVariable("userId") String userId, HttpServletRequest request) {
        if(serverId == null || serverId.equals("") || serverId == null || userId.equals("")) {
            logger.error("参数为空");
            ModelAndView modelAndViewSuccess = new ModelAndView("authFail");
            return modelAndViewSuccess;
        }
        Server server = serverService.get(Integer.valueOf(serverId));
        if(server == null) {
            logger.error("根据服务器Id查询服务器信息失败，serverId:" + serverId);
            ModelAndView modelAndViewSuccess = new ModelAndView("authFail");
            return modelAndViewSuccess;
        }
        UserInfo userInfoCondition = new UserInfo();
        userInfoCondition.setUserId(userId);
        userInfoCondition.setServerId(server.getId());
        UserInfo userInfo = userInfoService.getUserInfoByServerIdAndUserId(userInfoCondition);
        if(userInfo == null) {
            logger.error("根据用户Id、服务器Id查询用户信息失败，userId:" + userInfo.getUserId() + "serverId:" + userInfo.getServerId());
            ModelAndView modelAndViewSuccess = new ModelAndView("authFail");
            return modelAndViewSuccess;
        }
        // 查看是否已经申请过，并且正在审核中
        BindApply bindApplyCondition = new BindApply();
        bindApplyCondition.setServerId(server.getId());
        bindApplyCondition.setUserId(userInfo.getId());
        List<BindApply> bindApplyList = bindApplyService.getUnRevewByServerIdAndUserId(bindApplyCondition);
        if(bindApplyList != null && bindApplyList.size() > 0) {
            logger.info("用户id为" + userId + "的用户已经申请过，并且正在审核中");
            ModelAndView modelAndViewSuccess = new ModelAndView("authSuccess");
            return modelAndViewSuccess;
        }

        System.out.println("userId=" + userId);
		String webpageCode = request.getParameter("code");
		if(webpageCode == null) {
		    System.out.println("非法请求");
		    return null;
        }
        logger.info("webpageCode:" + webpageCode);
		String getWebpageAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
        getWebpageAccessTokenUrl = String.format(getWebpageAccessTokenUrl, appid, appsecret, webpageCode);
		JSONObject jsonObject = WxUtil.getDataFromWxServer(getWebpageAccessTokenUrl);
		if(jsonObject.get("errcode") == null) {
		    String webpageAccessToken = jsonObject.getString("access_token");
		    String openid = jsonObject.getString("openid");
            logger.info("webpageAccessToken:" + webpageAccessToken);
            logger.info("openid:" + openid);
		    // 拉取用户信息
            String getUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN";
            getUserInfoUrl = String.format(getUserInfoUrl, webpageAccessToken, openid);
            JSONObject jsonObjectGetUserInfo = WxUtil.getDataFromWxServer(getUserInfoUrl);
            if(jsonObjectGetUserInfo.get("errcode") == null)  {
                String nickname = jsonObjectGetUserInfo.getString("nickname");
                logger.info("nickname:" + nickname);
                BindApply bindApply = new BindApply();
                bindApply.setServerId(server.getId());
                bindApply.setUserId(userInfo.getId());
                bindApply.setWxNickname(nickname);
                bindApply.setStatus(0);
                bindApply.setOpenId(openid);
                bindApply.setApplyDate(new Date());
                int result = bindApplyService.add(bindApply);
                logger.info("result:" + result);
                if(result == 1) {
                    ModelAndView modelAndViewSuccess = new ModelAndView("authSuccess");
                    return modelAndViewSuccess;
                } else {
                    logger.error("添加绑定申请失败,result=" + result);
                }
            }
        }
        ModelAndView modelAndViewFail = new ModelAndView("authFail");
        return modelAndViewFail;
    }

    @GetMapping("/listBindUserPage")
    public String listBindUser() {
        return "listBindUser";
    }

    @ResponseBody
    @GetMapping("/getQRcode")
    public String getQRcode() {
        logger.info("getQRcode");
        // TODO 从session获取登录用户
//        User user  = userMapper.getReferenceById(1);
//        System.out.println(user);
//        WxUtil.session.put("user", user);
        JSONObject jsonObjectReturn = new JSONObject();
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
        jsonObjectSceneId.put("scene_str", "1");
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
//        JSONObject jsonObjectReturn = new JSONObject();
//        jsonObjectReturn.put("qrCodeUrl", "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=gQHw7zwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAyUW0ta1JESHBlVUMxbUFoSTF6YzYAAgQkF2NjAwSAOgkA");
//        return jsonObjectReturn.toString();
    }

//    根据客户id和用户id生成二维码
    @ResponseBody
    @GetMapping("/getQRcodeByUserId/{id}")
    public String getQRcodeByUserId(@PathVariable("id") String id) {
        logger.info("getQRcode");
        JSONObject jsonObjectReturn = new JSONObject();
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
        jsonObjectSceneId.put("scene_str", id);
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
}
