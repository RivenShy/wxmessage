package com.example.demo.controller;


import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.*;
import com.example.demo.result.R;
import com.example.demo.service.ChangeBindApplyService;
import com.example.demo.service.ServerService;
import com.example.demo.service.UserInfoService;
import com.example.demo.util.Constant;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/changeBindApply")
public class ChangeBindApplyController {

    private static Logger logger = Logger.getLogger(BindApplyController.class);

    @Autowired
    private ChangeBindApplyService changeBindApplyService;

    @Autowired
    private ServerService serverService;

    @Autowired
    private UserInfoService userInfoService;

    /*restful 部分*/
    @GetMapping("/changeBindApplys")
    public PageInfo<ChangeBindApply> list(@RequestParam(value = "start", defaultValue = "1") int start, @RequestParam(value = "size", defaultValue = "5") int size, @RequestParam(value = "deleted", defaultValue = "0") int deleted) throws Exception {
        PageHelper.startPage(start,size,"id desc");
        List<ChangeBindApply> hs = changeBindApplyService.list(deleted);
//        System.out.println(hs.size());

        PageInfo<ChangeBindApply> page = new PageInfo<>(hs,5); //5表示导航分页最多有5个，像 [1,2,3,4,5] 这样

        return page;
    }

    @PostMapping("/add")
    public String add(@RequestBody ChangeBindApply changeBindApply) throws Exception {
        JSONObject jsonObject = new JSONObject();
        if(StringUtil.isEmpty(changeBindApply.getWxNickname()) || StringUtil.isEmpty(changeBindApply.getOpenId()) || StringUtil.isEmpty(changeBindApply.getUserCode())) {
            logger.error("用户账号、微信昵称或openId不能为空");
            jsonObject.put(Constant.code, 200);
            jsonObject.put(Constant.success, false);
            jsonObject.put(Constant.data, null);
            jsonObject.put(Constant.msg, "用户账号、微信昵称或openId不能为空");
            return jsonObject.toString();
        }
        Server server = serverService.get(changeBindApply.getServerId());
        if(server == null) {
            logger.error("找不到客户服务器信息，id=" + changeBindApply.getServerId());
            jsonObject.put(Constant.code, 200);
            jsonObject.put(Constant.success, false);
            jsonObject.put(Constant.data, null);
            jsonObject.put(Constant.msg, "找不到客户服务器信息");
            return jsonObject.toString();
        }
        ChangeBindApply changeBindApplyUnReview = changeBindApplyService.getUnReviewByServerIdAndOpenIdAndUserCode(changeBindApply);
        if(changeBindApplyUnReview != null) {
            jsonObject.put(Constant.code, 200);
            jsonObject.put(Constant.success, true);
            jsonObject.put(Constant.data, null);
            jsonObject.put(Constant.msg, "更换绑定已申请成功，系统正在审核中!");
            return jsonObject.toString();
        }
        ChangeBindApply changeBindApplyArgs = new ChangeBindApply();
        changeBindApplyArgs.setServerId(server.getId());
        changeBindApplyArgs.setWxNickname(changeBindApply.getWxNickname());
        changeBindApplyArgs.setOpenId(changeBindApply.getOpenId());
        changeBindApplyArgs.setStatus(0);
        changeBindApplyArgs.setApplyDate(new Date());
        changeBindApplyArgs.setUserCode(changeBindApply.getUserCode());
        int result = changeBindApplyService.add(changeBindApplyArgs);
        if(result == 0) {
            logger.error("添加更换绑定申请失败，" + changeBindApplyArgs);
            jsonObject.put(Constant.code, 200);
            jsonObject.put(Constant.success, false);
            jsonObject.put(Constant.data, null);
            jsonObject.put(Constant.msg, "更换绑定申请失败");
            return jsonObject.toString();
        } else {
            jsonObject.put(Constant.code, 200);
            jsonObject.put(Constant.success, true);
            jsonObject.put(Constant.data, null);
            jsonObject.put(Constant.msg, "更换绑定申请成功");
            return jsonObject.toString();
        }
    }

    @PostMapping("/review")
    public String review(@RequestBody ChangeBindApply changeBindApply) throws Exception {
        JSONObject jsonObject = new JSONObject();
        ChangeBindApply changeBindApplyDB = changeBindApplyService.get(changeBindApply.getId());
        if(changeBindApplyDB == null) {
            logger.error("查询不到更换绑定信息，id=" + changeBindApply.getId());
            jsonObject.put(Constant.code, 200);
            jsonObject.put(Constant.success, false);
            jsonObject.put(Constant.data, null);
            jsonObject.put(Constant.msg, "查询不到更换绑定信息");
            return jsonObject.toString();
        }
        UserInfo userInfoArgs = new UserInfo();
        userInfoArgs.setServerId(changeBindApplyDB.getServerId());
        userInfoArgs.setUserId(changeBindApplyDB.getUserCode());
        UserInfo userInfo = userInfoService.getUserInfoByServerIdAndUserId(userInfoArgs);
        if(userInfo != null) {
            UserInfo userInfoUpdate = new UserInfo();
            userInfoUpdate.setId(userInfo.getId());
            userInfoUpdate.setWxNickname(changeBindApplyDB.getWxNickname());
            userInfoUpdate.setOpenId(changeBindApplyDB.getOpenId());
            int result = userInfoService.updateOpenIdAndNickName(userInfoUpdate);
            if(result == 1) {
                logger.info("更改用户的微信昵称和openId成功");
                // 更新状态为已审核
                result = changeBindApplyService.review(changeBindApplyDB.getId());
                if(result == 1) {
                    // 删除未绑定微信的用户账号记录
                    UnbindUser unbindUser = new UnbindUser();
                    unbindUser.setServerId(changeBindApplyDB.getServerId());
                    unbindUser.setUserId(changeBindApplyDB.getUserCode());
                    int resultDeleteUnbindUser = userInfoService.deleteUnbindUserByServerIdAndUserId(unbindUser);
                    if(resultDeleteUnbindUser == 0) {
                        logger.error("删除未绑定用户失败,userCode = " + changeBindApplyDB.getUserCode());
                    }
                    jsonObject.put(Constant.code, 200);
                    jsonObject.put(Constant.success, true);
                    jsonObject.put(Constant.data, null);
                    jsonObject.put(Constant.msg, "操作成功");
                    return jsonObject.toString();
                }  else {
                    logger.error("更改更换绑定申请的状态失败");
                    jsonObject.put(Constant.code, 200);
                    jsonObject.put(Constant.success, false);
                    jsonObject.put(Constant.data, null);
                    jsonObject.put(Constant.msg, "操作失败");
                    return jsonObject.toString();
                }
            } else {
                logger.error("更新用户微信昵称和openId信息失败，" + userInfoUpdate);
                jsonObject.put(Constant.code, 200);
                jsonObject.put(Constant.success, false);
                jsonObject.put(Constant.data, null);
                jsonObject.put(Constant.msg, "更新用户微信昵称和openId信息失败");
                return jsonObject.toString();
            }
        } else {
            logger.info("查询不到用户信息，将新增一个用户信息" + userInfoArgs);
            UserInfo userInfoAdd = new UserInfo();
            userInfoAdd.setServerId(changeBindApplyDB.getServerId());
            userInfoAdd.setUserId(changeBindApplyDB.getUserCode());
            userInfoAdd.setOpenId(changeBindApplyDB.getOpenId());
            userInfoAdd.setWxNickname(changeBindApplyDB.getWxNickname());
            int result = userInfoService.add(userInfoAdd);
            if(result == 1) {
                // 删除未绑定微信的用户账号记录
                UnbindUser unbindUser = new UnbindUser();
                unbindUser.setServerId(changeBindApplyDB.getServerId());
                unbindUser.setUserId(changeBindApplyDB.getUserCode());
                int resultDeleteUnbindUser = userInfoService.deleteUnbindUserByServerIdAndUserId(unbindUser);
                if(resultDeleteUnbindUser == 0) {
                    logger.error("删除未绑定用户失败,userCode = " + changeBindApplyDB.getUserCode());
                }
                jsonObject.put(Constant.code, 200);
                jsonObject.put(Constant.success, true);
                jsonObject.put(Constant.data, null);
                jsonObject.put(Constant.msg, "操作成功");
                return jsonObject.toString();
            } else {
                jsonObject.put(Constant.code, 200);
                jsonObject.put(Constant.success, false);
                jsonObject.put(Constant.data, null);
                jsonObject.put(Constant.msg, "添加用户失败");
                return jsonObject.toString();
            }
        }
    }

    @PostMapping("/remove")
    public R<Boolean> remove(@RequestParam Integer id) {
        if(id == null) {
            logger.error("id为null");
            return R.fail("id不能为空");
        }
        int result = changeBindApplyService.removeById(id);
        return R.status(result != 0);
    }
}
