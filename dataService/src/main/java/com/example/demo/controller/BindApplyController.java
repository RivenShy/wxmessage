package com.example.demo.controller;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.BindApply;
import com.example.demo.entity.UserInfo;
import com.example.demo.service.BindApplyService;
import com.example.demo.service.MessageService;
import com.example.demo.service.UserInfoService;
import com.example.demo.util.Constant;
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
    public PageInfo<BindApply> list(@RequestParam(value = "start", defaultValue = "1") int start, @RequestParam(value = "size", defaultValue = "5") int size) throws Exception {
        PageHelper.startPage(start,size,"id desc");
        List<BindApply> hs = bindApplyService.list();
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

    @ResponseBody
    @PostMapping("/review")
    public String review(@RequestBody BindApply bindApply) throws Exception {
        logger.info("id:" + bindApply.getId());
        int result = bindApplyService.review(bindApply.getId());
        logger.info("result:" + result);
        JSONObject jsonObject = new JSONObject();
        if(result == 1) {
            // 更新User表，绑定openId
            UserInfo userInfo = new UserInfo();
            BindApply bindApplyDB = bindApplyService.get(bindApply.getId());
            userInfo.setId(bindApplyDB.getUserId());
            userInfo.setOpenId(bindApplyDB.getOpenId());
            userInfo.setWxNickname(bindApplyDB.getWxNickname());
            int resultUpdateUser = userInfoService.updateOpenIdAndNickName(userInfo);
            logger.info("resultUpdateUser:" + resultUpdateUser);
            if(resultUpdateUser == 1) {
                jsonObject.put(Constant.code, 200);
                jsonObject.put(Constant.success, true);
                jsonObject.put(Constant.data, null);
                jsonObject.put(Constant.msg, "操作成功");
            } else {
                jsonObject.put(Constant.code, 200);
                jsonObject.put(Constant.success, false);
                jsonObject.put(Constant.data, null);
                jsonObject.put(Constant.msg, "更新用户失败");
            }
        } else {
            jsonObject.put(Constant.code, 200);
            jsonObject.put(Constant.success, false);
            jsonObject.put(Constant.data, null);
            jsonObject.put(Constant.msg, "审核失败");
        }
        return jsonObject.toString();
    }

    @PostMapping("/add")
    public String add(@RequestBody BindApply bindApply) throws Exception {
        int result = bindApplyService.add(bindApply);
        logger.info("result:" + result);
        return "success";
    }


}

