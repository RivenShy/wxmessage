package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.BindApply;
import com.example.demo.entity.Customer;
import com.example.demo.entity.Server;
import com.example.demo.entity.UserInfo;
import com.example.demo.service.BindApplyService;
import com.example.demo.service.CustomerService;
import com.example.demo.service.ServerService;
import com.example.demo.service.UserInfoService;
import com.example.demo.util.Constant;
import com.example.demo.util.WxUtil;
import com.github.pagehelper.StringUtil;
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
import java.io.IOException;
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

    @Autowired
    CustomerService customerService;
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
                    String openId = root.elementText("FromUserName");
                    String EventKey = root.elementText("EventKey");
                    logger.info("FromUserName:" + openId);
                    logger.info("ToUserName:" + ToUserName);
                    logger.info("EventKey:" + EventKey);
                    String serverIp = EventKey.split("_")[0];
                    if(serverIp == null) {
                        logger.error("serverIp 为null");
                        return;
                    }
                    String userId = EventKey.split("_")[1];
                    if(userId == null) {
                        logger.error("userId为null");
                        return;
                    }
                    sendBindApplyWxMessageToUser(serverIp, userId, openId);
                    // 关注事件
                } else if(Event.equals("subscribe")) {
                    String ToUserName = root.elementText("ToUserName");
                    String openId = root.elementText("FromUserName");
                    String EventKey = root.elementText("EventKey");
                    logger.info("FromUserName:" + openId);
                    logger.info("ToUserName:" + ToUserName);
                    logger.info("EventKey:" + EventKey);
                    // 判断是否是扫码后的关注事件
                    if(EventKey != null && !EventKey.equals("")) {
                        String serverIp = EventKey.split("_")[1];
                        if(serverIp == null) {
                            logger.error("serverIp 为null");
                            return;
                        }
                        String userId = EventKey.split("_")[2];
                        if(userId == null) {
                            logger.error("userId为null");
                            return;
                        }
                        sendBindApplyWxMessageToUser(serverIp, userId, openId);
                    }
                }
            }
        } else {
            logger.info("XML 数据包为null");
        }

    }

    private void sendBindApplyWxMessageToUser(String serverIp, String userId, String openId) {
        Server server = serverService.getServerByServerIp(serverIp);
        if(server == null) {
            logger.error("根据服务器ip查询服务器信息失败");
            return;
        }
        Customer customer = customerService.get(server.getCustomerId());
        if(customer == null) {
            logger.error("根据客户Id查询客户信息失败");
            return;
        }
        UserInfo userInfoArgs = new UserInfo();
        userInfoArgs.setServerId(server.getId());
        userInfoArgs.setUserId(userId);
        UserInfo userInfo = userInfoService.getUserInfoByServerIdAndUserId(userInfoArgs);
        String userName = userId;
        if(userInfo == null) {
            // 没有则新建该用户
            logger.error("根据服务器Id与用户代码查询信息失败");
//            return;
        } else {
            userName = userInfo.getUserName();
        }

//        String REDIRECT_URI = "http://www.szwd.online/wx/outhPage/" + serverIp + "/" + userId;
//        String outhUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
//        outhUrl = String.format(outhUrl, WxOfficialAccountController.appid, REDIRECT_URI);
        // todo，改成授权页面的url,等均仔给链接,serverIp、userCode，appid、secret也传
//        outhUrl = "http://www.szwd.online/wx/outhPage/" + serverIp + "/" + userId;
//        String outhUrl = "http://121.5.253.163:8080/#/userAuthorization?serverIp=%s&userCode=%s&appid=%s&secret=%s";
//        outhUrl = String.format(outhUrl, serverIp, userId, WxOfficialAccountController.appid, WxOfficialAccountController.appsecret);
        String outhUrl = "http://www.szwd.online:8009/#/userAuthorization?serverIp=%s&userCode=%s&appid=%s";
        outhUrl = String.format(outhUrl, serverIp, userId, WxOfficialAccountController.appid);
        logger.debug(outhUrl);

        WxUtil.sendOuthMsgToUser(openId, outhUrl, customer.getCustomerName(), userId, userName);
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

//    @GetMapping("/bindPage")
    public String bind() {
        return "bind";
    }

//    @GetMapping("/outhPage/{serverIp}/{userId}")
//    public ModelAndView outh(@PathVariable("serverIp") String serverIp, @PathVariable("userId") String userId, HttpServletRequest request) {
//        if(serverIp == null || serverIp.equals("") || userId == null || userId.equals("")) {
//            logger.error("参数为空");
//            ModelAndView modelAndViewSuccess = new ModelAndView("authFail");
//            return modelAndViewSuccess;
//        }
//        Server server = serverService.getServerByServerIp(serverIp);
//        if(server == null) {
//            logger.error("根据服务器Ip查询服务器信息失败，serverId:" + serverIp);
//            ModelAndView modelAndViewSuccess = new ModelAndView("authFail");
//            return modelAndViewSuccess;
//        }
//        UserInfo userInfoCondition = new UserInfo();
//        userInfoCondition.setUserId(userId);
//        userInfoCondition.setServerId(server.getId());
//        UserInfo userInfo = userInfoService.getUserInfoByServerIdAndUserId(userInfoCondition);
//        if(userInfo == null) {
//            logger.error("根据用户Id、服务器Id查询用户信息失败，userId:" + userId + "serverId:" + server.getId());
//            // 先放开，审核绑定发现没有用户，再插入数据库
////            ModelAndView modelAndViewSuccess = new ModelAndView("authFail");
////            return modelAndViewSuccess;
//        }
//        System.out.println("userId=" + userId);
//		String webpageCode = request.getParameter("code");
//		if(webpageCode == null) {
//		    System.out.println("非法请求");
//		    return null;
//        }
//        logger.info("webpageCode:" + webpageCode);
//		String getWebpageAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
//        getWebpageAccessTokenUrl = String.format(getWebpageAccessTokenUrl, appid, appsecret, webpageCode);
//		JSONObject jsonObject = WxUtil.getDataFromWxServer(getWebpageAccessTokenUrl);
//		if(jsonObject.get("errcode") == null) {
//		    String webpageAccessToken = jsonObject.getString("access_token");
//		    String openid = jsonObject.getString("openid");
//            logger.info("webpageAccessToken:" + webpageAccessToken);
//            logger.info("openid:" + openid);
//		    // 拉取用户信息
//            String getUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN";
//            getUserInfoUrl = String.format(getUserInfoUrl, webpageAccessToken, openid);
//            JSONObject jsonObjectGetUserInfo = WxUtil.getDataFromWxServer(getUserInfoUrl);
//            if(jsonObjectGetUserInfo.get("errcode") == null)  {
//                String nickname = jsonObjectGetUserInfo.getString("nickname");
//                String headimgurl = jsonObjectGetUserInfo.getString("headimgurl");
//                logger.info("nickname:" + nickname);
//                logger.info("headimgurl:" + headimgurl);
//                Customer customer = customerService.get(server.getId());
//                String userName = null;
//                if(userInfo != null) {
//                    userName = userInfo.getUserName() == null ? userInfo.getUserId() : userInfo.getUserName();
//                }
//                else {
//                    userName = userId;
//                }
//                // 查看是否已经申请过，并且正在审核中
//                BindApply bindApplyCondition = new BindApply();
//                bindApplyCondition.setServerId(server.getId());
//                bindApplyCondition.setUserCode(userId);
//                BindApply bindApplyDB = bindApplyService.getUnRevewByServerIdAndUserId(bindApplyCondition);
//                if(bindApplyDB != null) {
//                    logger.info("用户id为" + userId + "的用户已经申请过");
//                    // 同一微信重复绑定情况处理(后台未审核，后台已审核)
//                    String msg = "";
//                    if(nickname.equals(bindApplyDB.getWxNickname())) {
//                        if(bindApplyDB.getStatus() == 0) {
//                            msg = "审核中";
//                        } else {
//                            msg = "已审核";
//                        }
//                    } else {
//                        msg = "已有微信绑定";
//                        // 显示已申请绑定的微信昵称
//                        nickname = bindApplyDB.getWxNickname();
//                    }
//                    ModelAndView modelAndViewFail = new ModelAndView("authFail");
//
////                    modelAndViewSuccess.addObject("userName", userName);
//                    modelAndViewFail.addObject("userCode", bindApplyDB.getUserCode());
//                    modelAndViewFail.addObject("customerName", customer.getCustomerName());
//                    modelAndViewFail.addObject("nickname", nickname);
//                    modelAndViewFail.addObject("headimgurl", headimgurl);
//                    modelAndViewFail.addObject("msg", msg);
//                    return modelAndViewFail;
//                }
//                BindApply bindApply = new BindApply();
//                bindApply.setServerId(server.getId());
//                if(userInfo != null) {
//                    bindApply.setUserId(userInfo.getId());
//                }
//                bindApply.setWxNickname(nickname);
//                bindApply.setStatus(0);
//                bindApply.setOpenId(openid);
//                bindApply.setUserCode(userId);
//                bindApply.setApplyDate(new Date());
//                int result = bindApplyService.add(bindApply);
//                logger.info("result:" + result);
//                if(result == 1) {
//                    ModelAndView modelAndViewSuccess = new ModelAndView("authSuccess");
//                    modelAndViewSuccess.addObject("nickname", nickname);
//                    modelAndViewSuccess.addObject("headimgurl", headimgurl);
//                    modelAndViewSuccess.addObject("userName", userName);
//                    modelAndViewSuccess.addObject("customerName", customer.getCustomerName());
//                    modelAndViewSuccess.addObject("msg", "您已完成授权绑定申请，请等待系统审核！");
//                    return modelAndViewSuccess;
//                } else {
//                    logger.error("添加绑定申请失败,result=" + result);
//                }
//            }
//        }
//        ModelAndView modelAndViewFail = new ModelAndView("authFail");
//        return modelAndViewFail;
//    }


    @GetMapping("/outhPage/{serverIp}/{userId}")
    public void outh(@PathVariable("serverIp") String serverIp, @PathVariable("userId") String userId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("serverIp=" + serverIp + ",userCode=" + userId);
        String redirectUrl = "http://www.szwd.online:8009/#/bindPge?";
        if(serverIp == null || serverIp.equals("") || userId == null || userId.equals("")) {
            logger.error("参数为空");
            String suffix = "code=200&success=false&msg=" +  URLEncoder.encode("参数错误","UTF-8");
            response.sendRedirect(redirectUrl + suffix);
            return;
        }
        Server server = serverService.getServerByServerIp(serverIp);
        if(server == null) {
            logger.error("根据服务器Ip查询服务器信息失败，serverId:" + serverIp);
            String suffix = "code=200&success=false&msg=" + URLEncoder.encode("找不到客户服务器信息","UTF-8");

            response.sendRedirect(redirectUrl + suffix);
            return;
        }
        UserInfo userInfoCondition = new UserInfo();
        userInfoCondition.setUserId(userId);
        userInfoCondition.setServerId(server.getId());
        UserInfo userInfo = userInfoService.getUserInfoByServerIdAndUserId(userInfoCondition);
        if(userInfo == null) {
            logger.error("根据用户Id、服务器Id查询用户信息失败，userId:" + userId + "serverId:" + server.getId());
            // 先放开，审核绑定发现没有用户，再插入数据库
        }
        System.out.println("userId=" + userId);
        String webpageCode = request.getParameter("code");
        if(webpageCode == null) {
            System.out.println("非法请求");
            String suffix = "code=200&success=false&msg=" +  URLEncoder.encode("非法请求","UTF-8");
            response.sendRedirect(redirectUrl + suffix);
            return;
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
                String headimgurl = jsonObjectGetUserInfo.getString("headimgurl");
                logger.info("nickname:" + nickname);
                logger.info("headimgurl:" + headimgurl);
                Customer customer = customerService.get(server.getId());
                String userName = null;
                // 查看是否已经申请过，并且正在审核中
                BindApply bindApplyCondition = new BindApply();
                bindApplyCondition.setServerId(server.getId());
                bindApplyCondition.setUserCode(userId);
                BindApply bindApplyDB = bindApplyService.getUnRevewByServerIdAndUserId(bindApplyCondition);
                if(bindApplyDB != null) {
                    logger.info("用户id为" + userId + "的用户已经申请过");
                    // 同一微信重复绑定情况处理(后台未审核，后台已审核)
                    String msg = "";
                    if(nickname.equals(bindApplyDB.getWxNickname())) {
                        if(bindApplyDB.getStatus() == 0) {
                            msg = "您已经申请过绑定，请等待系统审核。";
                        } else {
                            msg = "您已经申请过绑定，系统已审核通过。";
                        }
                    } else {
                        msg = "用户“" + bindApplyDB.getUserCode() + "“" + "已与“" + bindApplyDB.getWxNickname() + "”微信绑定，请先联系管理员解除绑定后，再执行此操作。";
                        // 显示已申请绑定的微信昵称
                        nickname = bindApplyDB.getWxNickname();
                    }
                    String suffix = "code=200&success=false&msg=%s&userCode=%s&customerName=%s&nickname=%s&headimgurl=%s";
                    suffix = String.format(suffix, URLEncoder.encode(msg,"UTF-8"), URLEncoder.encode(bindApplyDB.getUserCode(),"UTF-8"),URLEncoder.encode(customer.getCustomerName(),"UTF-8") , URLEncoder.encode(nickname,"UTF-8"), headimgurl);
                    response.sendRedirect(redirectUrl + suffix);
                    return;
                }
                BindApply bindApply = new BindApply();
                bindApply.setServerId(server.getId());
                if(userInfo != null) {
                    bindApply.setUserId(userInfo.getId());
                }
                bindApply.setWxNickname(nickname);
                bindApply.setStatus(0);
                bindApply.setOpenId(openid);
                bindApply.setUserCode(userId);
                bindApply.setApplyDate(new Date());
                int result = bindApplyService.add(bindApply);
                logger.info("result:" + result);
                if(result == 1) {
                    String msg = "您已授权申请绑定成功，请等待系统审核！";
                    String suffix = "code=200&success=true&msg=%s&userCode=%s&customerName=%s&nickname=%s&headimgurl=%s";
                    suffix = String.format(suffix, URLEncoder.encode(msg,"UTF-8"), URLEncoder.encode(userId,"UTF-8"), URLEncoder.encode(customer.getCustomerName(),"UTF-8"), URLEncoder.encode(nickname,"UTF-8"), headimgurl);
                    response.sendRedirect(redirectUrl + suffix);
                    return;
                } else {
                    logger.error("添加绑定申请失败,result=" + result);
                    String suffix = "code=200&success=false&msg=" + URLEncoder.encode("添加绑定申请失败","UTF-8");
                    response.sendRedirect(redirectUrl + suffix);
                    return;
                }
            }
        }
        logger.error("请求微信服务器错误");
        String suffix = "code=200&success=false&msg="+  URLEncoder.encode("请求微信服务器错误","UTF-8");
        response.sendRedirect(redirectUrl + suffix);
    }

    @PostMapping("/outhBindApply")
    @ResponseBody
    public String outhBindApply(@RequestBody BindApply bindApply) {
        logger.info(bindApply);
        JSONObject jsonObjectReturn = new JSONObject();
        String serverIp = bindApply.getServerIp();
        String userId = bindApply.getUserCode();
        String nickname = bindApply.getWxNickname();
        String openid = bindApply.getOpenId();
        if(StringUtil.isEmpty(serverIp) || StringUtil.isEmpty(userId) || StringUtil.isEmpty(nickname) || StringUtil.isEmpty(openid)) {
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "参数错误");
            return jsonObjectReturn.toString();
        }
        Server server = serverService.getServerByServerIp(serverIp);
        if(server == null) {
            logger.error("根据服务器Ip查询服务器信息失败，serverIp:" + serverIp);
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "查询不到客户服务器信息");
            return jsonObjectReturn.toString();
        }
        UserInfo userInfoCondition = new UserInfo();
        userInfoCondition.setUserId(userId);
        userInfoCondition.setServerId(server.getId());
        UserInfo userInfo = userInfoService.getUserInfoByServerIdAndUserId(userInfoCondition);
        if(userInfo == null) {
            logger.error("根据用户Id、服务器Id查询用户信息失败，userId:" + userId + "serverId:" + server.getId());
            // 先放开，审核绑定发现没有用户，再插入数据库
        }
        Customer customer = customerService.get(server.getId());
        BindApply bindApplyCondition = new BindApply();
        bindApplyCondition.setServerId(server.getId());
        bindApplyCondition.setUserCode(userId);
        BindApply bindApplyDB = bindApplyService.getUnRevewByServerIdAndUserId(bindApplyCondition);
        if(bindApplyDB != null) {
            logger.info("用户id为" + userId + "的用户已经申请过");
            // 同一微信重复绑定情况处理(后台未审核，后台已审核)
            String msg = "";
//            if(nickname.equals(bindApplyDB.getWxNickname())) {
            if(openid.equals(bindApplyDB.getOpenId())) {
                if(bindApplyDB.getStatus() == 0) {
                    msg = "审核中";
                } else {
                    msg = "已审核";
                }
            } else {
                msg = "已有微信绑定";
            }
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, customer.getCustomerName());
            jsonObjectReturn.put(Constant.msg, msg);
            return jsonObjectReturn.toString();
        }
        BindApply bindApplyArgs = new BindApply();
        bindApplyArgs.setServerId(server.getId());
        if(userInfo != null) {
            bindApplyArgs.setUserId(userInfo.getId());
        }
        bindApplyArgs.setWxNickname(nickname);
        bindApplyArgs.setStatus(0);
        bindApplyArgs.setOpenId(openid);
        bindApplyArgs.setUserCode(userId);
        bindApplyArgs.setApplyDate(new Date());
        int result = bindApplyService.add(bindApplyArgs);
        logger.info("result:" + result);
        if(result == 1) {
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, true);
            jsonObjectReturn.put(Constant.data, customer.getCustomerName());
            jsonObjectReturn.put(Constant.msg, "您已完成授权绑定申请，请等待系统审核！");
            return jsonObjectReturn.toString();
        } else {
            logger.error("添加绑定申请失败,result=" + result);
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, customer.getCustomerName());
            jsonObjectReturn.put(Constant.msg, "添加绑定申请失败");
            return jsonObjectReturn.toString();
        }
    }

    @GetMapping("/listBindUserPage")
    public String listBindUser() {
        return "listBindUser";
    }

//    @ResponseBody
//    @GetMapping("/getQRcode")
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
//    @ResponseBody
//    @GetMapping("/getQRcodeByUserId/{id}")
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
            logger.error("errcode：" + errcode);
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "向微信服务器发送Post请求出错");
            return jsonObjectReturn.toString();
        }
    }
}
