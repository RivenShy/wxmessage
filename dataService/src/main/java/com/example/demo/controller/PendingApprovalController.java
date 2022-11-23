package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.PendingApproval;
import com.example.demo.entity.PendingApprovalDetail;
import com.example.demo.service.IPendingApprovalService;
import com.example.demo.util.Constant;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pendingApproval")
public class PendingApprovalController {

    private static Logger logger = Logger.getLogger(PendingApprovalController.class);

    @Autowired
    private IPendingApprovalService pendingApprovalService;

    @GetMapping("/list")
    public String list() {
        JSONObject jsonObject = new JSONObject();
        logger.info("查询待审批数据");
        List<PendingApproval> list = pendingApprovalService.list();
        System.out.println(list);
        if(list != null) {
            jsonObject.put(Constant.code, 200);
            jsonObject.put(Constant.success, true);
            jsonObject.put(Constant.data, list);
            jsonObject.put(Constant.msg, "查询成功");
            return jsonObject.toString();
        } else{
            jsonObject.put(Constant.code, 200);
            jsonObject.put(Constant.success, false);
            jsonObject.put(Constant.data, null);
            jsonObject.put(Constant.msg, "查询失败");
            return jsonObject.toString();
        }
    }

    @GetMapping("/getAdTototalCountByUserCode/{userCode}")
    public String getByUserCode(@PathVariable String userCode) {
        JSONObject jsonObject = new JSONObject();
        logger.info("查询今日已审、待审");
        PendingApproval pendingApproval = pendingApprovalService.getByUserCode(userCode);
        logger.info(pendingApproval);
        // for Test
//        PendingApproval pendingApproval = new PendingApproval();
//        pendingApproval.setJobuser("jobUserTest");
//        pendingApproval.setTotalCount(1);
//        pendingApproval.setAdcount(1);
        //
        if(pendingApproval != null) {
            jsonObject.put(Constant.code, 200);
            jsonObject.put(Constant.success, true);
            jsonObject.put(Constant.data, pendingApproval);
            jsonObject.put(Constant.msg, "查询成功");
            return jsonObject.toString();
        } else {
            jsonObject.put(Constant.code, 200);
            jsonObject.put(Constant.success, false);
            jsonObject.put(Constant.data, null);
            jsonObject.put(Constant.msg, "查询失败");
            return jsonObject.toString();
        }
    }

    @GetMapping("/getAverageTimeByUserCode/{userCode}")
    public String getAverageTimeByUserCode(@PathVariable String userCode) {
        JSONObject jsonObject = new JSONObject();
        logger.info("查询待平均耗时");
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("UserId", userCode);
        PendingApproval pendingApproval = pendingApprovalService.getAverageTime(hashMap);
        logger.info(pendingApproval);
//        PendingApproval pendingApproval = new PendingApproval();
//        pendingApproval.setJobuser("jobUserTest");
//        pendingApproval.setTotalCount(1);
//        pendingApproval.setAdcount(1);
//        pendingApproval.setReturn_value(9527);
        if(pendingApproval != null) {
            jsonObject.put(Constant.code, 200);
            jsonObject.put(Constant.success, true);
            jsonObject.put(Constant.data, pendingApproval);
            jsonObject.put(Constant.msg, "查询成功");
            return jsonObject.toString();
        } else {
            jsonObject.put(Constant.code, 200);
            jsonObject.put(Constant.success, false);
            jsonObject.put(Constant.data, null);
            jsonObject.put(Constant.msg, "查询失败");
            return jsonObject.toString();
        }
    }


    @GetMapping("/getTotalApprovalRank")
    public String getTotalApprovalRank() {
        JSONObject jsonObject = new JSONObject();
        logger.info("查询今日排行（已审批数目排行）");
        List<PendingApproval> list = pendingApprovalService.getRank();
        logger.info(list);
        // 先返回假数据，做测试
//        List<PendingApproval> list = new ArrayList<>();
//        PendingApproval pendingApproval = new PendingApproval();
//        pendingApproval.setJobuser("jobuserRank");
//        pendingApproval.setTotalCount(10086);
//        list.add(pendingApproval);
        //
        if(list != null) {
            jsonObject.put(Constant.code, 200);
            jsonObject.put(Constant.success, true);
            jsonObject.put(Constant.data, list);
            jsonObject.put(Constant.msg, "查询成功");
            return jsonObject.toString();
        } else{
            jsonObject.put(Constant.code, 200);
            jsonObject.put(Constant.success, false);
            jsonObject.put(Constant.data, null);
            jsonObject.put(Constant.msg, "查询失败");
            return jsonObject.toString();
        }
    }
}
