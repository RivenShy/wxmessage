package com.example.demo.controller;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.BindApply;
import com.example.demo.entity.UnbindUser;
import com.example.demo.entity.UserInfo;
import com.example.demo.result.R;
import com.example.demo.service.BindApplyService;
import com.example.demo.service.MessageService;
import com.example.demo.service.UserInfoService;
import com.example.demo.util.Constant;
import com.github.pagehelper.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@RestController
@RequestMapping("/bindApply")
public class BindApplyController {

    private static Logger logger = Logger.getLogger(BindApplyController.class);

    @Autowired
    private BindApplyService bindApplyService;

    @Autowired
    private UserInfoService userInfoService;

    /*restful 部分*/
    @GetMapping("/bindApplys")
    public PageInfo<BindApply> list(@RequestParam(value = "start", defaultValue = "1") int start, @RequestParam(value = "size", defaultValue = "5") int size, @RequestParam(value = "deleted", defaultValue = "0") int deleted) throws Exception {
        PageHelper.startPage(start,size,"id desc");
        List<BindApply> hs = bindApplyService.list(deleted);
//        System.out.println(hs.size());

        PageInfo<BindApply> page = new PageInfo<>(hs,5); //5表示导航分页最多有5个，像 [1,2,3,4,5] 这样

        return page;
    }

//    @ResponseBody
//    @PostMapping("/review")
//    public String review(@RequestParam(value = "id") int id) throws Exception {
//        System.out.println("id:" + id);
//        int result = bindApplyService.review(id);
//        return null;
//    }

//    @ResponseBody
//    @PostMapping("/review")
//    public String review(@RequestBody BindApply bindApply) throws Exception {
//        logger.info("id:" + bindApply.getId());
//        int result = bindApplyService.review(bindApply.getId());
//        logger.info("result:" + result);
//        JSONObject jsonObject = new JSONObject();
//        BindApply bindApplyDB = bindApplyService.get(bindApply.getId());
//        if(result == 1) {
//            if(bindApplyDB.getUserId() != 0)  {
//                // 更新User表，绑定openId
//                UserInfo userInfo = new UserInfo();
//                userInfo.setId(bindApplyDB.getUserId());
//                userInfo.setOpenId(bindApplyDB.getOpenId());
//                userInfo.setWxNickname(bindApplyDB.getWxNickname());
//                int resultUpdateUser = userInfoService.updateOpenIdAndNickName(userInfo);
//                logger.info("resultUpdateUser:" + resultUpdateUser);
//                if(resultUpdateUser == 1) {
//                    // 删除未绑定微信的用户账号记录
//                    UnbindUser unbindUser = new UnbindUser();
//                    unbindUser.setServerId(bindApplyDB.getServerId());
//                    unbindUser.setUserId(bindApplyDB.getUserCode());
//                    int resultDeleteUnbindUser = userInfoService.deleteUnbindUserByServerIdAndUserId(unbindUser);
//                    if(resultDeleteUnbindUser == 0) {
//                        logger.error("删除未绑定用户失败,userCode = " + bindApplyDB.getUserCode());
//                    }
//                    jsonObject.put(Constant.code, 200);
//                    jsonObject.put(Constant.success, true);
//                    jsonObject.put(Constant.data, null);
//                    jsonObject.put(Constant.msg, "操作成功");
//                } else {
//                    jsonObject.put(Constant.code, 200);
//                    jsonObject.put(Constant.success, false);
//                    jsonObject.put(Constant.data, null);
//                    jsonObject.put(Constant.msg, "更新用户失败");
//                }
//            }
//            // 服务器还没登记还用户信息，则新建该用户
//            else {
//                UserInfo userInfoArgs = new UserInfo();
//                userInfoArgs.setServerId(bindApplyDB.getServerId());
//                userInfoArgs.setUserId(bindApplyDB.getUserCode());
//                userInfoArgs.setOpenId(bindApplyDB.getOpenId());
//                userInfoArgs.setWxNickname(bindApplyDB.getWxNickname());
//                result = userInfoService.add(userInfoArgs);
//                if(result == 1) {
//                    // 删除未绑定微信的用户账号记录
//                    UnbindUser unbindUser = new UnbindUser();
//                    unbindUser.setServerId(bindApplyDB.getServerId());
//                    unbindUser.setUserId(bindApplyDB.getUserCode());
//                    int resultDeleteUnbindUser = userInfoService.deleteUnbindUserByServerIdAndUserId(unbindUser);
//                    if(resultDeleteUnbindUser == 0) {
//                        logger.error("删除未绑定用户失败,userCode = " + bindApplyDB.getUserCode());
//                    }
//                    jsonObject.put(Constant.code, 200);
//                    jsonObject.put(Constant.success, true);
//                    jsonObject.put(Constant.data, null);
//                    jsonObject.put(Constant.msg, "操作成功");
//                } else {
//                    jsonObject.put(Constant.code, 200);
//                    jsonObject.put(Constant.success, false);
//                    jsonObject.put(Constant.data, null);
//                    jsonObject.put(Constant.msg, "添加用户失败");
//                }
//            }
//        } else {
//            jsonObject.put(Constant.code, 200);
//            jsonObject.put(Constant.success, false);
//            jsonObject.put(Constant.data, null);
//            jsonObject.put(Constant.msg, "审核失败");
//        }
//        return jsonObject.toString();
//    }

    @ResponseBody
    @PostMapping("/review")
    public R review(@RequestBody BindApply bindApply) throws Exception {
        logger.info("id:" + bindApply.getId());
//        logger.info("result:" + result);
        BindApply bindApplyDB = bindApplyService.get(bindApply.getId());
        if(bindApplyDB == null) {
            return R.fail("查询绑定信息失败");
        }
        if(bindApplyDB.getUserId() != 0 || bindApply.getUserId() != 0)  {
            // 更新User表，绑定openId
            UserInfo userInfo = null;
            if(bindApplyDB.getUserId() != 0) {
                userInfo = userInfoService.get(bindApplyDB.getUserId());
            } else {
                userInfo = userInfoService.get(bindApply.getUserId());
                if(userInfo.getServerId() != bindApplyDB.getServerId()) {
                    return R.fail("不能选择与申请企业不对应的用户");
                }
            }
            userInfo.setOpenId(bindApplyDB.getOpenId());
            userInfo.setWxNickname(bindApplyDB.getWxNickname());
            int resultUpdateUser = userInfoService.updateOpenIdAndNickName(userInfo);
            logger.info("resultUpdateUser:" + resultUpdateUser);
            if(resultUpdateUser == 1) {
                // 删除未绑定微信的用户账号记录
                UnbindUser unbindUser = new UnbindUser();
                unbindUser.setServerId(bindApplyDB.getServerId());
                unbindUser.setUserId(userInfo.getUserId());
                int resultDeleteUnbindUser = userInfoService.deleteUnbindUserByServerIdAndUserId(unbindUser);
                if(resultDeleteUnbindUser == 0) {
                    logger.error("删除未绑定用户失败,userCode = " + userInfo.getUserId());
                }
                int result = bindApplyService.review(bindApply.getId());
                if(result == 0) {
                    return R.fail("审核失败");
                }
                return R.success("操作成功");
            } else {
                return R.fail("更新用户失败");
            }
        }
        // 服务器还没登记还用户信息，则新建该用户
        else {
            if(StringUtil.isEmpty(bindApplyDB.getUserCode())) {
                return R.fail("用户账号不能为空");
            }
            UserInfo userInfoArgs = new UserInfo();
            userInfoArgs.setServerId(bindApplyDB.getServerId());
            userInfoArgs.setUserId(bindApplyDB.getUserCode());
            userInfoArgs.setOpenId(bindApplyDB.getOpenId());
            userInfoArgs.setWxNickname(bindApplyDB.getWxNickname());
            int result = userInfoService.add(userInfoArgs);
            if(result == 1) {
                // 删除未绑定微信的用户账号记录
                UnbindUser unbindUser = new UnbindUser();
                unbindUser.setServerId(bindApplyDB.getServerId());
                unbindUser.setUserId(bindApplyDB.getUserCode());
                UnbindUser unbindUserDB = userInfoService.selectUnbindUserByServerIdAndUserCode(unbindUser);
                if(unbindUserDB != null) {
                    int resultDeleteUnbindUser = userInfoService.deleteUnbindUserByServerIdAndUserId(unbindUser);
                    if (resultDeleteUnbindUser == 0) {
                        logger.error("删除未绑定用户失败,userCode = " + bindApplyDB.getUserCode());
                    }
                }
                result = bindApplyService.review(bindApply.getId());
                if(result == 0) {
                    return R.fail("审核失败");
                }
                return R.success("操作成功");
            } else {
                return R.fail("添加用户失败");
            }
        }
    }


    @PostMapping("/add")
    public String add(@RequestBody BindApply bindApply) throws Exception {
        int result = bindApplyService.add(bindApply);
        logger.info("result:" + result);
        return "success";
    }

    @PostMapping("/reBind")
    public String reBind(@RequestBody BindApply bindApply) throws Exception {
        logger.info(bindApply);
        JSONObject jsonObject = new JSONObject();
        if(bindApply.getId() == 0 || bindApply.getServerId() == 0 || StringUtil.isEmpty(bindApply.getUserCode())) {
            logger.error("参数错误");
            jsonObject.put(Constant.code, 200);
            jsonObject.put(Constant.success, false);
            jsonObject.put(Constant.data, null);
            jsonObject.put(Constant.msg, "参数错误");
            return jsonObject.toString();
        }
        BindApply bindApplyDB = bindApplyService.get(bindApply.getId());
        if(bindApplyDB == null) {
            logger.error("根据Id查找绑定信息失败,id = " + bindApplyDB.getId());
            jsonObject.put(Constant.code, 200);
            jsonObject.put(Constant.success, false);
            jsonObject.put(Constant.data, null);
            jsonObject.put(Constant.msg, "找不到对应的绑定申请信息");
            return jsonObject.toString();
        }
        UserInfo userInfoArgs = new UserInfo();
        userInfoArgs.setServerId(bindApply.getServerId());
        userInfoArgs.setUserId(bindApply.getUserCode());
        UserInfo userInfoDB = userInfoService.getUserInfoByServerIdAndUserId(userInfoArgs);
        userInfoDB.setWxNickname(bindApplyDB.getWxNickname());
        userInfoDB.setOpenId(bindApplyDB.getOpenId());
        int result = userInfoService.updateOpenIdAndNickName(userInfoDB);
        if(result == 0) {
            logger.error("更新用户的微信昵称和openId失败,用户id = " + userInfoDB.getId());
            jsonObject.put(Constant.code, 200);
            jsonObject.put(Constant.success, false);
            jsonObject.put(Constant.data, null);
            jsonObject.put(Constant.msg, "更新用户的微信昵称和openId失败");
            return jsonObject.toString();
        } else {
            // 删除未绑定微信的用户账号记录
            UnbindUser unbindUser = new UnbindUser();
            unbindUser.setServerId(bindApplyDB.getServerId());
            unbindUser.setUserId(bindApplyDB.getUserCode());
            int resultDeleteUnbindUser = userInfoService.deleteUnbindUserByServerIdAndUserId(unbindUser);
            if(resultDeleteUnbindUser == 0) {
                logger.error("删除未绑定用户失败,userCode = " + bindApplyDB.getUserCode());
            }
            jsonObject.put(Constant.code, 200);
            jsonObject.put(Constant.success, true);
            jsonObject.put(Constant.data, null);
            jsonObject.put(Constant.msg, "操作成功");
            return jsonObject.toString();
        }
//        保持删除状态，避免有多个微信用户对应相同账号为已审核的绑定申请
//        BindApply bindApplyArgs = new BindApply();
//        bindApplyArgs.setId(bindApply.getId());
//        bindApplyArgs.setUserId(userInfoDB.getId());
//        bindApplyArgs.setUserCode(userInfoDB.getUserId());
//        result = bindApplyService.reBindByServerIdAndUserId(bindApplyArgs);
//        if(result == 0) {
//            logger.error("更新绑定申请信息失败：" + bindApplyArgs);
//            jsonObject.put(Constant.code, 200);
//            jsonObject.put(Constant.success, false);
//            jsonObject.put(Constant.data, null);
//            jsonObject.put(Constant.msg, "更新绑定申请信息失败");
//            return jsonObject.toString();
//        }
    }

    @PostMapping("/remove")
    public R<Boolean> remove(@RequestParam Integer id) {
        if(id == null) {
            logger.error("id为null");
            return R.fail("id不能为空");
        }
        int result = bindApplyService.removeById(id);
        return R.status(result != 0);
    }
}

