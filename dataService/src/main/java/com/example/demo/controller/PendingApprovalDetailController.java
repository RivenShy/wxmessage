package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.PendingApprovalDetail;
import com.example.demo.service.IPendingApprovalDetailService;
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
@RequestMapping("/pendingApprovalDetail")
public class PendingApprovalDetailController {

    private static Logger logger = Logger.getLogger(PendingApprovalDetailController.class);

    @Autowired
    private IPendingApprovalDetailService pendingApprovalDetailService;


    @GetMapping("/listByUserCode/{userCode}")
    public String listByUserCode(@PathVariable String userCode) {
        JSONObject jsonObject = new JSONObject();
        logger.info("查询待审批数据");
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("UserId", userCode);
        List<PendingApprovalDetail> list = pendingApprovalDetailService.listByUserCode(hashMap);
        System.out.println(list);
        // 先返回假数据，做测试
//        List<PendingApprovalDetail> list = new ArrayList<>();
//        PendingApprovalDetail pendingApprovalDetail = new PendingApprovalDetail();
//        pendingApprovalDetail.setAuditName("auditNameTest");
//        pendingApprovalDetail.setBillName("billNameTest");
//        pendingApprovalDetail.setProjectName("projectNamTest");
//        pendingApprovalDetail.setDelayHour(12);
//        list.add(pendingApprovalDetail);
        //
        if(list != null) {
            jsonObject.put(Constant.code, 200);
            jsonObject.put(Constant.success, true);
            jsonObject.put(Constant.data, list);
            jsonObject.put(Constant.msg, "查询成功");
            return jsonObject.toString();
        } else {
            jsonObject.put(Constant.code, 200);
            jsonObject.put(Constant.success, false);
            jsonObject.put(Constant.data, list);
            jsonObject.put(Constant.msg, "查询失败");
            return jsonObject.toString();
        }
    }
}
