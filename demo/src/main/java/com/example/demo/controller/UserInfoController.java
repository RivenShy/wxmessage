package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.Server;
import com.example.demo.entity.UserDepm;
import com.example.demo.entity.UserInfo;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.ServerService;
import com.example.demo.service.UserService;
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
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    private static Logger logger = Logger.getLogger(UserController.class);

    @Autowired
    UserService userService;

    @Autowired
    ServerService serverService;

    @GetMapping("/listUserInfo")
    public ModelAndView listUserInfo() {
        return new ModelAndView("listUserInfo");
    }

    @GetMapping("/userInfos")
    public PageInfo<UserInfo> list(@RequestParam(value = "start", defaultValue = "1") int start, @RequestParam(value = "size", defaultValue = "5") int size) throws Exception {
        PageHelper.startPage(start,size,"id desc");
        List<UserInfo> hs = userService.list();
        System.out.println(hs.size());

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
        UserInfo userInfoDB = userService.getUserInfoByServerIdAndUserId(userInfoCondition);
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
}
