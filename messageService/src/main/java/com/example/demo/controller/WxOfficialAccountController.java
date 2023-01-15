package com.example.demo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.*;
import com.example.demo.entity.wx.TextReplyService;
import com.example.demo.result.R;
import com.example.demo.service.BindApplyService;
import com.example.demo.service.CustomerService;
import com.example.demo.service.ServerService;
import com.example.demo.service.UserInfoService;
import com.example.demo.util.Constant;
import com.example.demo.util.QrCodeUtil;
import com.example.demo.util.WxUtil;
import com.github.pagehelper.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/wx")
public class WxOfficialAccountController {

    private static Logger logger = Logger.getLogger(WxOfficialAccountController.class);

    public static final String TOKEN = "projectManagerSystem";

    /**
     * 客户端登录，缓存guid前缀
     */
    private static final String PCclietLogin_Redis_Prefix = "pcClientRedisPrefix-";

    // 公众号appid、appsecret
    public static String appid;
    public static String appsecret;
    public static String accessTokenTest = "abc";
    @Value("${wx.appid}")
	public void setAppid(String appid){
		this.appid= appid;
	}
	@Value("${wx.appsecret}")
	public void setAppsecret(String appsecret){
		this.appsecret= appsecret;
	}
	@Value("${wx.accessTokenTest}")
	public void setAccessTokenTest(String accessTokenTest){
		this.accessTokenTest= accessTokenTest;
	}
//    public static final String appid = "wx226ffc0b68fa17e9";
//    public static final String appsecret = "4a4037b79e0390da5a4d8cb8ff5014f0";
//    public static final String accessTokenTest = "";
    // todo for test
//    public static final String accessTokenTest = "64_g5gj74RBGXdPB0hee5ir8lNqo1j9zLaRvaGJO1PSarW2k35V_To3SaZ516-8XTpTo1DiLlSWWnfSyDxBf5mLXcEZbFnfNHLRlBgzMxXrOda-6SAGXIUiqvbWnrcTNCbAFAEYN";

    // 公众号测试号appid、appsecret
//    public static final String appid = "wx1b112629d06f809e";
//    public static final String appsecret = "df3d2364046eae4a00bb4a5cec6333e8";
//    public static final String accessTokenTest = "";
//    public static final String accessTokenTest = "64_w0ddSOqdT4HCHPxynvsVkaNlDELnQROuX16GylnM3A3-VvGuXlCoWWcuXvkYF93ATGRFTGUBp_U35R5A2gjgqVYjlYgzq6zQmT4OvbNSdlL6loJXx1WjVR8O2zMFPAgAIABLW";

    @Autowired
    BindApplyService bindApplyService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    ServerService serverService;

    @Autowired
    CustomerService customerService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

//    @GetMapping("/test")
    public void test(HttpServletResponse response) throws IOException {
//            response.sendRedirect("https://www.baidu.com/");

        response.sendRedirect("https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx1b112629d06f809e&redirect_uri=http://86vgjd.natappfree.cc/wx/outhPage/1/2&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect");
        return;
    }

    /**
     * 验证消息的确来自微信服务器，此接口用于微信公众号后台服务器认证使用，GET请求
     * @param request
     * @param response
     */
    @GetMapping("/validate")
    public void validateMessageFromWxServer(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        System.out.println("进来了wx/validate");
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");
        if(signature == null || timestamp == null || nonce == null || echostr == null) {
            System.out.println("请求参数有误");
            return;
        }
//        1）将token、timestamp、nonce三个参数进行字典序排序
        String[] strArray = {TOKEN, timestamp, nonce};
        Arrays.sort(strArray);
//        2）将三个参数字符串拼接成一个字符串进行sha1加密
        StringBuilder stringBuilder = new StringBuilder();
        for(String str : strArray) {
            stringBuilder.append(str);
        }
        String encryption = WxUtil.getSha1(stringBuilder.toString());
//        3）开发者获得加密后的字符串可与 signature 对比，标识该请求来源于微信
        if(encryption.equals(signature)) {
            out.print(echostr);
            out.flush();
            out.close();
        }
    }


    @PostMapping("/validate")
    public void handlerWechatEvent(HttpServletRequest request, HttpServletResponse response, @RequestBody String xmlData) throws Exception {
        //
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
                    int size = EventKey.split("_").length;
                    String serverIp = EventKey.split("_")[0];
                    if(serverIp == null) {
                        logger.error("serverIp 为null");
                        return;
                    }
                    String userId = "";
                    if(size == 2) {
                        userId = EventKey.split("_")[1];
                        if (userId == null) {
                            logger.error("userId为null");
                            return;
                        }
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
                        int size = EventKey.split("_").length;
                        String serverIp = EventKey.split("_")[1];
                        if(serverIp == null) {
                            logger.error("serverIp 为null");
                            return;
                        }
//                        String userId = EventKey.split("_")[2];
//                        if(userId == null) {
//                            logger.error("userId为null");
//                            return;
//                        }
                        String userId = "";
                        if(size == 2) {
                            userId = EventKey.split("_")[1];
                            if (userId == null) {
                                logger.error("userId为null");
                                return;
                            }
                        }
                        sendBindApplyWxMessageToUser(serverIp, userId, openId);
                    }
                }
            } else if(MsgType.equals("text")) {
                // 文本消息处理
//                Map<String, String> requestMap = WechatMessageUtils.parseXml(request);
//                return textReplyService.reply(requestMap);
                String ToUserName = root.elementText("ToUserName");
                String FromUserName = root.elementText("FromUserName");
                String Content = root.elementText("Content");
                Map<String, String> requestMap = new HashMap<>();
                requestMap.put(TextReplyService.FROM_USER_NAME, FromUserName);
                requestMap.put(TextReplyService.TO_USER_NAME, ToUserName);
                requestMap.put(TextReplyService.CONTENT, Content);
                String respMessage = new TextReplyService().reply(requestMap);
                if (StringUtils.isBlank(respMessage)) {
                    logger.info("不回复消息");
                    return;
                }
                PrintWriter out = null;
                response.setCharacterEncoding("UTF-8");
                out = response.getWriter();
                out.write(respMessage);
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
//        outhUrl = "http://www.szwd.online/wx/outhPage/" + serverIp + "/" + userId;
//        String outhUrl = "http://121.5.253.163:8080/#/userAuthorization?serverIp=%s&userCode=%s&appid=%s&secret=%s";
//        outhUrl = String.format(outhUrl, serverIp, userId, WxOfficialAccountController.appid, WxOfficialAccountController.appsecret);
        String outhUrl = null;
        if(!StringUtil.isEmpty(userId)) {
            outhUrl = "http://www.szwd.online:8009/#/userAuthorization?serverIp=%s&userCode=%s&appid=%s";
            outhUrl = String.format(outhUrl, serverIp, userId, WxOfficialAccountController.appid);
        } else {
            outhUrl = "http://www.szwd.online:8009/#/userAuthorizationByUserId?serverIp=%s&appid=%s";
            outhUrl = String.format(outhUrl, serverIp, WxOfficialAccountController.appid);
        }
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
                // 查看当前账号是否已经绑定正在申请绑定的微信
                if(userInfo != null) {
                    if(openid.equals(userInfo.getOpenId())) {
                        String msg = "您好， <span style=\"color: #820000;\">" + nickname + "</span>，您已经成功与账号<span style=\"color: #820000;\">" + userId + "（" + customer.getCustomerName() +  ")</span>" + "绑定！绑定后享有以下权利：";
                        String suffix = "code=200&success=true&msg=%s&userCode=%s&customerName=%s&nickname=%s&headimgurl=%s&customerLogo=%s";
                        suffix = String.format(suffix, URLEncoder.encode(msg,"UTF-8"), URLEncoder.encode(userId,"UTF-8"), URLEncoder.encode(customer.getCustomerName(),"UTF-8"), URLEncoder.encode(nickname,"UTF-8"), headimgurl, customer.getLogoPath());
                        response.sendRedirect(redirectUrl + suffix);
                        return;
                    }
                }
                // 查看是否已经申请过，并且正在审核中
                BindApply bindApplyCondition = new BindApply();
                bindApplyCondition.setServerId(server.getId());
                bindApplyCondition.setUserCode(userId);
                BindApply bindApplyDB = bindApplyService.getUnRevewByServerIdAndUserId(bindApplyCondition);
//                String suffix = "code=200&success=false&msg=%s&userCode=%s&customerName=%s&nickname=%s&headimgurl=%s&customerLogo=%s";
                String suffix = "code=200&success=review&msg=%s&userCode=%s&customerName=%s&nickname=%s&headimgurl=%s&customerLogo=%s";
                if(bindApplyDB != null) {
                    logger.info("用户账号为" + userId + "的用户已经申请过");
                    // 同一微信重复绑定情况处理(后台未审核，后台已审核)
                    String msg = "";
                    if(openid.equals(bindApplyDB.getOpenId())) {
                        if(bindApplyDB.getStatus() == 0) {
                            msg = "您好，<span style=\"color: #820000;\">" + nickname + "</span>，您已申请绑定账号<span style=\"color: #820000;\">" + userId + "（" + customer.getCustomerName() +  ")</span>，申请正在审核中，预计一个工作日内完成。";
                        } else {
                            msg = "您已经申请过绑定，系统已审核通过。";
                            logger.error("审核绑定申请，应该已经绑定成功");
                        }
                        suffix = String.format(suffix, URLEncoder.encode(msg,"UTF-8"), URLEncoder.encode(bindApplyDB.getUserCode(),"UTF-8"),
                                URLEncoder.encode(customer.getCustomerName(),"UTF-8") , URLEncoder.encode(nickname,"UTF-8"), headimgurl, customer.getLogoPath());
                    } else {
//                        msg = "用户“" + bindApplyDB.getUserCode() + "“" + "已与“" + bindApplyDB.getWxNickname() + "”微信绑定，请先联系管理员解除绑定后，再执行此操作。";
                        msg = "您扫描的账号<span style=\"color: #820000;\">" + bindApplyDB.getUserCode() + "</span>隶属于<span style=\"color: #820000;\">" + customer.getCustomerName() + "</span>。此账号已与微信<span style=\"color: #820000;\">" + bindApplyDB.getWxNickname() + "</span>绑定。请联系软件系统管理员解除绑定，再重新扫码。";
//                        suffix = "code=200&success=false&msg=%s&userCode=%s&customerName=%s&nickname=%s&headimgurl=%s&serverId=%s&openId=%s&customerLogo=%s";
                        suffix = "code=200&success=false&msg=%s&userCode=%s&customerName=%s&nickname=%s&headimgurl=%s&serverId=%s&openId=%s&customerLogo=%s";
                        suffix = String.format(suffix, URLEncoder.encode(msg,"UTF-8"), URLEncoder.encode(bindApplyDB.getUserCode(),"UTF-8"),
                                URLEncoder.encode(customer.getCustomerName(),"UTF-8") , URLEncoder.encode(nickname,"UTF-8"), headimgurl, server.getId(), openid, customer.getLogoPath());
                        // 显示已申请绑定的微信昵称
//                        nickname = bindApplyDB.getWxNickname();
                    }
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
                    String msg = "您好，<span style=\"color: #820000;\">" + nickname + "</span>，您已申请绑定账号<span style=\"color: #820000;\">" + userId + "（" + customer.getCustomerName() +  ")</span>，申请正在审核中，预计一个工作日内完成。";
                    suffix = String.format(suffix, URLEncoder.encode(msg,"UTF-8"), URLEncoder.encode(userId,"UTF-8"), URLEncoder.encode(customer.getCustomerName(),"UTF-8"), URLEncoder.encode(nickname,"UTF-8"), headimgurl, customer.getLogoPath());
                    response.sendRedirect(redirectUrl + suffix);
                    return;
                } else {
                    logger.error("添加绑定申请失败,result=" + result);
                    String suffixErr = "code=200&success=false&msg=" + URLEncoder.encode("添加绑定申请失败,服务器错误","UTF-8");
                    response.sendRedirect(redirectUrl + suffixErr);
                    return;
                }
            }
        }
        logger.error("请求微信服务器错误");
        String suffix = "code=200&success=false&msg="+  URLEncoder.encode("请求微信服务器错误","UTF-8");
        response.sendRedirect(redirectUrl + suffix);
    }

    @GetMapping("/outhBind")
    public void outhBind(@RequestParam(value = "serverIp") String serverIp, @RequestParam(value="userId",required=false) String userId, @RequestParam(value="userName",required=false) String userName, @RequestParam(value="userPassword",required=false) String userPassword, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("serverIp=" + serverIp + ",userCode=" + userId);
        String redirectUrl = "http://www.szwd.online:8009/#/bindPge?";
        if(StringUtil.isEmpty(serverIp)) {
            logger.error("参数错误，serverIp为空");
            String suffix = "code=200&success=false&msg=" +  URLEncoder.encode("参数错误，serverIp为空","UTF-8");
            response.sendRedirect(redirectUrl + suffix);
            return;
        }
        if(StringUtil.isEmpty(userId) && StringUtil.isEmpty(userName)) {
            logger.error("参数错误，用户账号和用户名不能同时为空");
            String suffix = "code=200&success=false&msg=" +  URLEncoder.encode("参数错误，用户账号和用户名不能同时为空","UTF-8");
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
        UserInfo userInfo = null;
        if(!StringUtil.isEmpty(userId)) {
            UserInfo userInfoCondition = new UserInfo();
            userInfoCondition.setUserId(userId);
            userInfoCondition.setServerId(server.getId());
            userInfo = userInfoService.getUserInfoByServerIdAndUserId(userInfoCondition);
            if(userInfo == null) {
                logger.error("根据用户Id、服务器Id查询用户信息失败，userId:" + userId + "serverId:" + server.getId());
                // 先放开，审核绑定发现没有用户，再插入数据库
            }
        }
        System.out.println("userId=" + userId);
        String webpageCode = request.getParameter("code");
        if(webpageCode == null) {
            logger.error("非法请求");
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
//                String userName = null;
                // 查看当前账号是否已经绑定正在申请绑定的微信
                if(userInfo != null) {
                    if(openid.equals(userInfo.getOpenId())) {
                        String msg = "您好， <span style=\"color: #820000;\">" + nickname + "</span>，您已经成功与账号<span style=\"color: #820000;\">" + userId + "（" + customer.getCustomerName() +  ")</span>" + "绑定！绑定后享有以下权利：";
                        String suffix = "code=200&success=true&msg=%s&userCode=%s&customerName=%s&nickname=%s&headimgurl=%s&customerLogo=%s";
                        suffix = String.format(suffix, URLEncoder.encode(msg,"UTF-8"), URLEncoder.encode(userId,"UTF-8"), URLEncoder.encode(customer.getCustomerName(),"UTF-8"), URLEncoder.encode(nickname,"UTF-8"), headimgurl, customer.getLogoPath());
                        response.sendRedirect(redirectUrl + suffix);
                        return;
                    }
                }
                String suffix = "code=200&success=review&msg=%s&userCode=%s&customerName=%s&nickname=%s&headimgurl=%s&customerLogo=%s";
                // 查看是否已经申请过，并且正在审核中

                BindApply bindApplyCondition = new BindApply();
                bindApplyCondition.setServerId(server.getId());
                BindApply bindApplyDB = null;
                if(!StringUtil.isEmpty(userId)) {
                    bindApplyCondition.setUserCode(userId);
                    bindApplyDB = bindApplyService.getUnRevewByServerIdAndUserId(bindApplyCondition);
                    if (bindApplyDB != null) {
                        logger.info("用户账号为" + userId + "的用户已经申请过");
                        // 同一微信重复绑定情况处理(后台未审核，后台已审核)
                        String msg = "";
                        if (openid.equals(bindApplyDB.getOpenId())) {
                            if (bindApplyDB.getStatus() == 0) {
                                msg = "您好，<span style=\"color: #820000;\">" + nickname + "</span>，您已申请绑定账号<span style=\"color: #820000;\">" + userId + "（" + customer.getCustomerName() + ")</span>，申请正在审核中，预计一个工作日内完成。";
                            } else {
                                msg = "您已经申请过绑定，系统已审核通过。";
                            }
                            suffix = String.format(suffix, URLEncoder.encode(msg, "UTF-8"), URLEncoder.encode(bindApplyDB.getUserCode(), "UTF-8"),
                                    URLEncoder.encode(customer.getCustomerName(), "UTF-8"), URLEncoder.encode(nickname, "UTF-8"), headimgurl, customer.getLogoPath());
                        } else {
                            msg = "您扫描的账号<span style=\"color: #820000;\">" + bindApplyDB.getUserCode() + "</span>隶属于<span style=\"color: #820000;\">" + customer.getCustomerName() + "</span>。此账号已与微信<span style=\"color: #820000;\">" + bindApplyDB.getWxNickname() + "</span>绑定。请联系软件系统管理员解除绑定，再重新扫码。";
                            suffix = "code=200&success=false&msg=%s&userCode=%s&customerName=%s&nickname=%s&headimgurl=%s&serverId=%s&openId=%s&customerLogo=%s";
                            suffix = String.format(suffix, URLEncoder.encode(msg, "UTF-8"), URLEncoder.encode(bindApplyDB.getUserCode(), "UTF-8"),
                                    URLEncoder.encode(customer.getCustomerName(), "UTF-8"), URLEncoder.encode(nickname, "UTF-8"), headimgurl, server.getId(), openid, customer.getLogoPath());
                        }
                        response.sendRedirect(redirectUrl + suffix);
                        return;
                    }
                } else if(!StringUtil.isEmpty(userName)){
                    bindApplyCondition.setUserName(userName);
                    bindApplyDB = bindApplyService.getUnRevewByServerIdAndUserName(bindApplyCondition);
                    if (bindApplyDB != null) {
                        logger.info("用户名称为" + userName + "的用户已经申请过");
                        // 同一微信重复绑定情况处理(后台未审核，后台已审核)
                        String msg = "";
//                        if (nickname.equals(bindApplyDB.getWxNickname())) {
//                            if (bindApplyDB.getStatus() == 0) {
//                                msg = "您好，<span style=\"color: #820000;\">" + nickname + "</span>，您已申请绑定，用户名称：<span style=\"color: #820000;\">" + userName + "（" + customer.getCustomerName() + ")</span>，申请正在审核中，预计一个工作日内完成。";
//                            } else {
//                                msg = "您已经申请过绑定，系统已审核通过。";
//                            }
//                            suffix = String.format(suffix, URLEncoder.encode(msg, "UTF-8"), URLEncoder.encode(bindApplyDB.getUserCode(), "UTF-8"),
//                                    URLEncoder.encode(customer.getCustomerName(), "UTF-8"), URLEncoder.encode(nickname, "UTF-8"), headimgurl, customer.getLogoPath());
//                        } else {
//                            msg = "您申请绑定的账号<span style=\"color: #820000;\">" + bindApplyDB.getUserCode() + "</span>隶属于<span style=\"color: #820000;\">" + customer.getCustomerName() + "</span>。此账号已与微信<span style=\"color: #820000;\">" + bindApplyDB.getWxNickname() + "</span>绑定。请联系软件系统管理员解除绑定，再重新扫码。";
//                            suffix = "code=200&success=false&msg=%s&userCode=%s&customerName=%s&nickname=%s&headimgurl=%s&serverId=%s&openId=%s&customerLogo=%s";
//                            suffix = String.format(suffix, URLEncoder.encode(msg, "UTF-8"), URLEncoder.encode(bindApplyDB.getUserCode(), "UTF-8"),
//                                    URLEncoder.encode(customer.getCustomerName(), "UTF-8"), URLEncoder.encode(nickname, "UTF-8"), headimgurl, server.getId(), openid, customer.getLogoPath());
//                        }
                        if (bindApplyDB.getStatus() == 0) {
                            msg = "您好，<span style=\"color: #820000;\">" + nickname + "</span>，您已申请绑定，用户名称：<span style=\"color: #820000;\">" + userName + "（" + customer.getCustomerName() + ")</span>，申请正在审核中，预计一个工作日内完成。";
                        } else {
                            msg = "您已经申请过绑定，系统已审核通过。";
                        }
                        suffix = String.format(suffix, URLEncoder.encode(msg, "UTF-8"), URLEncoder.encode(bindApplyDB.getUserName(), "UTF-8"),
                                URLEncoder.encode(customer.getCustomerName(), "UTF-8"), URLEncoder.encode(nickname, "UTF-8"), headimgurl, customer.getLogoPath());
                        response.sendRedirect(redirectUrl + suffix);
                        return;
                    }
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
                bindApply.setUserName(userName);
                bindApply.setUserPassword(userPassword);
                int result = bindApplyService.add(bindApply);
                logger.info("result:" + result);
                if(result == 1) {
                    String msg = "";
                    if(!StringUtil.isEmpty(userId)) {
                        msg = "您好，<span style=\"color: #820000;\">" + nickname + "</span>，您已申请绑定账号<span style=\"color: #820000;\">" + userId + "（" + customer.getCustomerName() +  ")</span>，申请正在审核中，预计一个工作日内完成。";
                    } else {
                        userId = userName;
                        msg = "您好，<span style=\"color: #820000;\">" + nickname + "</span>，您已申请绑定，用户名称：<span style=\"color: #820000;\">" + userId + "（" + customer.getCustomerName() +  ")</span>，申请正在审核中，预计一个工作日内完成。";
                    }
                    suffix = String.format(suffix, URLEncoder.encode(msg,"UTF-8"), URLEncoder.encode(userId,"UTF-8"), URLEncoder.encode(customer.getCustomerName(),"UTF-8"), URLEncoder.encode(nickname,"UTF-8"), headimgurl, customer.getLogoPath());
                    response.sendRedirect(redirectUrl + suffix);
                    return;
                } else {
                    logger.error("添加绑定申请失败,result=" + result);
                    String suffixErr = "code=200&success=false&msg=" + URLEncoder.encode("添加绑定申请失败,服务器错误","UTF-8");
                    response.sendRedirect(redirectUrl + suffixErr);
                    return;
                }
            }
        }
        logger.error("请求微信服务器错误");
        String suffix = "code=200&success=false&msg="+  URLEncoder.encode("请求微信服务器错误","UTF-8");
        response.sendRedirect(redirectUrl + suffix);
    }

//    @PostMapping("/outhBindApply")
//    @ResponseBody
//    public String outhBindApply(@RequestBody BindApply bindApply) {
//        logger.info(bindApply);
//        JSONObject jsonObjectReturn = new JSONObject();
//        String serverIp = bindApply.getServerIp();
//        String userId = bindApply.getUserCode();
//        String nickname = bindApply.getWxNickname();
//        String openid = bindApply.getOpenId();
//        if(StringUtil.isEmpty(serverIp) || StringUtil.isEmpty(userId) || StringUtil.isEmpty(nickname) || StringUtil.isEmpty(openid)) {
//            jsonObjectReturn.put(Constant.code, 200);
//            jsonObjectReturn.put(Constant.success, false);
//            jsonObjectReturn.put(Constant.data, null);
//            jsonObjectReturn.put(Constant.msg, "参数错误");
//            return jsonObjectReturn.toString();
//        }
//        Server server = serverService.getServerByServerIp(serverIp);
//        if(server == null) {
//            logger.error("根据服务器Ip查询服务器信息失败，serverIp:" + serverIp);
//            jsonObjectReturn.put(Constant.code, 200);
//            jsonObjectReturn.put(Constant.success, false);
//            jsonObjectReturn.put(Constant.data, null);
//            jsonObjectReturn.put(Constant.msg, "查询不到客户服务器信息");
//            return jsonObjectReturn.toString();
//        }
//        UserInfo userInfoCondition = new UserInfo();
//        userInfoCondition.setUserId(userId);
//        userInfoCondition.setServerId(server.getId());
//        UserInfo userInfo = userInfoService.getUserInfoByServerIdAndUserId(userInfoCondition);
//        if(userInfo == null) {
//            logger.error("根据用户Id、服务器Id查询用户信息失败，userId:" + userId + "serverId:" + server.getId());
//            // 先放开，审核绑定发现没有用户，再插入数据库
//        }
//        Customer customer = customerService.get(server.getId());
//        BindApply bindApplyCondition = new BindApply();
//        bindApplyCondition.setServerId(server.getId());
//        bindApplyCondition.setUserCode(userId);
//        BindApply bindApplyDB = bindApplyService.getUnRevewByServerIdAndUserId(bindApplyCondition);
//        if(bindApplyDB != null) {
//            logger.info("用户id为" + userId + "的用户已经申请过");
//            // 同一微信重复绑定情况处理(后台未审核，后台已审核)
//            String msg = "";
////            if(nickname.equals(bindApplyDB.getWxNickname())) {
//            if(openid.equals(bindApplyDB.getOpenId())) {
//                if(bindApplyDB.getStatus() == 0) {
//                    msg = "审核中";
//                } else {
//                    msg = "已审核";
//                }
//            } else {
//                msg = "已有微信绑定";
//            }
//            jsonObjectReturn.put(Constant.code, 200);
//            jsonObjectReturn.put(Constant.success, false);
//            jsonObjectReturn.put(Constant.data, customer.getCustomerName());
//            jsonObjectReturn.put(Constant.msg, msg);
//            return jsonObjectReturn.toString();
//        }
//        BindApply bindApplyArgs = new BindApply();
//        bindApplyArgs.setServerId(server.getId());
//        if(userInfo != null) {
//            bindApplyArgs.setUserId(userInfo.getId());
//        }
//        bindApplyArgs.setWxNickname(nickname);
//        bindApplyArgs.setStatus(0);
//        bindApplyArgs.setOpenId(openid);
//        bindApplyArgs.setUserCode(userId);
//        bindApplyArgs.setApplyDate(new Date());
//        int result = bindApplyService.add(bindApplyArgs);
//        logger.info("result:" + result);
//        if(result == 1) {
//            jsonObjectReturn.put(Constant.code, 200);
//            jsonObjectReturn.put(Constant.success, true);
//            jsonObjectReturn.put(Constant.data, customer.getCustomerName());
//            jsonObjectReturn.put(Constant.msg, "您已完成授权绑定申请，请等待系统审核！");
//            return jsonObjectReturn.toString();
//        } else {
//            logger.error("添加绑定申请失败,result=" + result);
//            jsonObjectReturn.put(Constant.code, 200);
//            jsonObjectReturn.put(Constant.success, false);
//            jsonObjectReturn.put(Constant.data, customer.getCustomerName());
//            jsonObjectReturn.put(Constant.msg, "添加绑定申请失败");
//            return jsonObjectReturn.toString();
//        }
//    }

//    @GetMapping("/listBindUserPage")
    public String listBindUser() {
        return "listBindUser";
    }

//    @ResponseBody
//    @GetMapping("/getQRcode")
    public String getQRcode() {
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

    @ResponseBody
    @PostMapping("/getLoginQrCode")
    public R getLoginQrCode(@RequestBody UserInfo userInfo)  {
        String guid = userInfo.getGuid();
        if(StringUtil.isEmpty(guid) || guid.length() < 4) {
            logger.error("参数错误, guid为空");
            return R.fail("参数错误");
        }
        String uuidTail2 = guid.substring(guid.length() - 2);
        uuidTail2 = uuidTail2.toUpperCase();
        String uuidHead2 = guid.substring(0, 2);
        uuidHead2 = uuidHead2.toUpperCase();
        String uuidMerge = uuidHead2 + uuidTail2;
        String uuidMergeEnCode = WxUtil.signEnCode(uuidMerge);
        String sign = userInfo.getSign();
        if(sign == null || !sign.equals(uuidMergeEnCode)) {
            logger.error("无权限，sign = " + sign +",signCheck = " + uuidMerge);
//            return R.fail("无权限");
        }
        GuidInfo guidInfo = new GuidInfo();
        guidInfo.setGuid(guid);
        guidInfo.setStatus(GuidInfo.enumGuidInfoStatus.EGS_NotYetScan.getName());
        String json = JSONObject.toJSONString(guidInfo);
        // 设置5分钟过期
//        stringRedisTemplate.opsForValue().set(PCclietLogin_Redis_Prefix + guid,json,300, TimeUnit.SECONDS);
        String redirectUrl = "http://www.szwd.online/wx/loginOuth?guid=" + guid;
//        String redirectUrl = "http://hqt2uc.natappfree.cc/wx/loginOuth?guid=" + guid;

        String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
        url = String.format(url, WxOfficialAccountController.appid, redirectUrl);
        byte[] data = QrCodeUtil.getQrCodeByteArray(url);
        if(data == null) {
            logger.error("获取二维码二进制流失败");
            return R.fail("获取二维码失败");
        }
        return R.data(data);
    }

    @GetMapping("/loginOuth")
    public void loginOuth(@RequestParam("guid") String guid, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String redirectPage = "http://www.szwd.online:8009/#/selectUser?";
        String suffix = "";
        if(StringUtil.isEmpty(guid)) {
            logger.error("参数错误, guid为空");
            suffix = "code=400&success=false&msg=%s";
            suffix = String.format(suffix, URLEncoder.encode("参数错误, guid为空","UTF-8"));
            response.sendRedirect(redirectPage + suffix);
            return;
        }
        // 接口权限
        if(!WxUtil.checkGuidAuth(guid)) {
            logger.error("无权限");
            suffix = "code=400&success=false&msg=%s";
            suffix = String.format(suffix, URLEncoder.encode("无权限","UTF-8"));
            response.sendRedirect(redirectPage + suffix);
            return;
        }
        //
        String redisGuidInfo = stringRedisTemplate.opsForValue().get(PCclietLogin_Redis_Prefix + guid);
        if(!StringUtil.isEmpty(redisGuidInfo)) {
            logger.error("guid不存在或已过期，登录失败");
            suffix = "code=400&success=false&msg=%s";
            suffix = String.format(suffix, URLEncoder.encode("二维码已过期,请重新获取二维码进行扫描","UTF-8"));
            response.sendRedirect(redirectPage + suffix);
            return;
        }
//        GuidInfo guidInfoCache = JSON.parseObject(redisGuidInfo, GuidInfo.class);
//        if(guidInfoCache == null) {
//            logger.error("转换json数据失败");
//            request.setAttribute("msg", "服务器错误");
//            request.setAttribute("success", false);
//            request.setAttribute("data", null);
//            suffix = "code=400&success=false&msg=%s";
//            suffix = String.format(suffix, URLEncoder.encode("服务器错误","UTF-8"));
//            response.sendRedirect(redirectPage + suffix);
//        }
//        if(!guidInfoCache.getStatus().equals(GuidInfo.enumGuidInfoStatus.EGS_NotYetScan.getName())) {
//            logger.error("该二维码已被扫描过");
//            suffix = "code=400&success=false&msg=%s";
//            suffix = String.format(suffix, URLEncoder.encode("该二维码已被扫描过，请重新获取二维码进行扫码登录","UTF-8"));
//            response.sendRedirect(redirectPage + suffix);
//            return;
//        }
//        guidInfo.setStatus(GuidInfo.enumGuidInfoStatus.EGS_Scaned.getName());
//        String json = JSONObject.toJSONString(guidInfo);
//        Long expire = stringRedisTemplate.getExpire(PCclietLogin_Redis_Prefix + guid, TimeUnit.SECONDS);
//        stringRedisTemplate.opsForValue().set(PCclietLogin_Redis_Prefix + guid, json, expire, TimeUnit.SECONDS);
        //
        GuidInfo guidInfo = new GuidInfo();
        guidInfo.setGuid(guid);
        guidInfo.setStatus(GuidInfo.enumGuidInfoStatus.EGS_Scaned.getName());
        String json = JSONObject.toJSONString(guidInfo);
        // 设置5分钟过期
        stringRedisTemplate.opsForValue().set(PCclietLogin_Redis_Prefix + guid,json,300, TimeUnit.SECONDS);

        String webpageCode = request.getParameter("code");
        if(webpageCode == null) {
            logger.error("非法请求");
            return;
        }
        logger.info("webpageCode:" + webpageCode);
        String getWebpageAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
        getWebpageAccessTokenUrl = String.format(getWebpageAccessTokenUrl, WxOfficialAccountController.appid, WxOfficialAccountController.appsecret, webpageCode);
        JSONObject jsonObject = WxUtil.getDataFromWxServer(getWebpageAccessTokenUrl);
        if(jsonObject.get("errcode") == null) {
            String webpageAccessToken = jsonObject.getString("access_token");
            String openid = jsonObject.getString("openid");
            logger.info("webpageAccessToken:" + webpageAccessToken);
            logger.info("openid:" + openid);
            guidInfo.setStatus(GuidInfo.enumGuidInfoStatus.EGS_Outhed.getName());
            json = JSONObject.toJSONString(guidInfo);
            Long expire = stringRedisTemplate.getExpire(PCclietLogin_Redis_Prefix + guid, TimeUnit.SECONDS);
            stringRedisTemplate.opsForValue().set(PCclietLogin_Redis_Prefix + guid,json,expire, TimeUnit.SECONDS);
            suffix = "code=200&success=true&msg=%s&guid=%s&openId=%s";
            suffix = String.format(suffix, URLEncoder.encode("微信授权成功","UTF-8"), URLEncoder.encode(guid,"UTF-8"), URLEncoder.encode(openid,"UTF-8"));
            response.sendRedirect(redirectPage + suffix);
            return;
        } else {
            logger.error("微信授权失败");
            suffix = "code=400&success=false&msg=%s";
            suffix = String.format(suffix, URLEncoder.encode("该二维码已被扫描过，请重新获取二维码进行扫码登录","UTF-8"));
            response.sendRedirect(redirectPage + suffix);
            return;
        }
    }

    /**
     * PC客户端登录
     */
    @ResponseBody
    @PostMapping("/pcLogin")
    public R pcLogin(@RequestBody UserInfo userInfo)  {
        String guid = userInfo.getGuid();
        int serverId = userInfo.getServerId();
        String userId = userInfo.getUserId();
        if(StringUtil.isEmpty(guid) || serverId < 0 || StringUtil.isEmpty(userId)) {
            logger.error("参数错误");
            return R.fail("参数错误");
        }
        UserInfo userInfoDB = userInfoService.getUserInfoByServerIdAndUserId(userInfo);
        if(userInfoDB == null) {
            logger.error("查找用户信息失败");
            return R.fail("查找用户信息失败");
        }
        String redisGuidInfo = stringRedisTemplate.opsForValue().get(PCclietLogin_Redis_Prefix + guid);
        if(StringUtil.isEmpty(redisGuidInfo)) {
            logger.error("guid不存在或已过期，登录失败");
            return R.fail("登录失败或二维码登录已过期");
        } else {
            GuidInfo guidInfoCache = JSON.parseObject(redisGuidInfo, GuidInfo.class);
            if(guidInfoCache == null) {
                logger.error("转换json数据失败");
                return R.fail("转换json数据失败");
            }
            if(!StringUtil.isEmpty(guidInfoCache.getServerIp())) {
                logger.error("已选择过账号登录，不能再选择登录，需要重新扫码登录");
                return R.fail("请重新扫码登录");
            }
//            guidInfoCache.setGuid(guid);
            guidInfoCache.setAccount(userInfoDB.getUserId());
            Server server = serverService.get(serverId);
            if(server == null) {
                logger.error("查询服务器信息失败");
                return R.fail("查询服务器信息失败");
            }
            guidInfoCache.setServerIp(server.getServerIp());
            guidInfoCache.setStatus(GuidInfo.enumGuidInfoStatus.EGS_Logined.getName());
            String json = JSONObject.toJSONString(guidInfoCache);
            Long expire = stringRedisTemplate.getExpire(PCclietLogin_Redis_Prefix + guid, TimeUnit.SECONDS);
            stringRedisTemplate.opsForValue().set(PCclietLogin_Redis_Prefix + guid,json,expire, TimeUnit.SECONDS);
            return R.success("授权登录成功");
        }
    }

    @ResponseBody
    @PostMapping("/getGuidInfo")
    public R getGuidInfo(@RequestBody UserInfo userInfo) {
        String guid = userInfo.getGuid();
        if(StringUtil.isEmpty(guid) || guid.length() < 2) {
            logger.error("参数错误, guid为空");
            return R.fail("参数错误");
        }
        // 接口权限
        if(!WxUtil.checkGuidAuth(guid)) {
            logger.error("无权限");
            return R.fail("无权限");
        }
        String redisGuidInfo = stringRedisTemplate.opsForValue().get(PCclietLogin_Redis_Prefix + guid);
        if(StringUtil.isEmpty(redisGuidInfo)) {
            logger.error("guid不存在或已过期");
            return R.fail("guid不存在或已过期");
        }
        GuidInfo guidInfo = JSON.parseObject(redisGuidInfo, GuidInfo.class);
        if(guidInfo == null) {
            logger.error("转换json数据失败");
            return R.fail("转换json数据失败");
        }
        return R.data(guidInfo);
    }


    @ResponseBody
    @PostMapping("/getUserInfoByOpenId")
    public R getUserInfoByOpenId(@RequestBody UserInfo userInfo) {
        String guid = userInfo.getGuid();
        String openid = userInfo.getOpenId();
        if(StringUtil.isEmpty(guid) ||StringUtil.isEmpty(openid)) {
            logger.error("参数错误，guid和openId不能为空");
            return R.fail("参数错误");
        }
        String redisGuidInfo = stringRedisTemplate.opsForValue().get(PCclietLogin_Redis_Prefix + guid);
        if(StringUtil.isEmpty(redisGuidInfo)) {
            logger.error("参数错误，guid不存在或已过期");
            return R.fail("二维码已过期，请重新扫码登录");
        }
        List<UserInfo> userInfoList = userInfoService.selectUserInfoByOpenId(openid);
        if(userInfoList == null) {
            logger.error("查询用户信息失败");
            return R.fail("查询用户信息失败");
        }
        return R.data(userInfoList);
    }
}
