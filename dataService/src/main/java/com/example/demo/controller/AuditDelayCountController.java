package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.AuditDelayCount;
import com.example.demo.entity.PendingApproval;
import com.example.demo.service.IAuditDelayCountService;
import com.example.demo.service.IPendingApprovalService;
import com.example.demo.util.Constant;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auditDelayCount")
public class AuditDelayCountController {

    private static Logger logger = Logger.getLogger(AuditDelayCountController.class);

    @Autowired
    private IAuditDelayCountService auditDelayCountService;

    @GetMapping("/list")
    public String list() {
        JSONObject jsonObject = new JSONObject();
        logger.info("查询用户待审批数量和延迟24小时的数量");
        List<AuditDelayCount> list = auditDelayCountService.list();
        if(list == null) {
            logger.error("查询失败");
            jsonObject.put(Constant.code, 200);
            jsonObject.put(Constant.success, false);
            jsonObject.put(Constant.data, list);
            jsonObject.put(Constant.msg, "查询失败");
            return jsonObject.toString();
        } else {
            jsonObject.put(Constant.code, 200);
            jsonObject.put(Constant.success, true);
            jsonObject.put(Constant.data, list);
            jsonObject.put(Constant.msg, "查询成功");
            return jsonObject.toString();
        }
    }

    @GetMapping("/getByUserCode/{userCode}")
    public String getByUserCode(@PathVariable String userCode) {
        JSONObject jsonObject = new JSONObject();
        logger.info("查询某用户待审批数量和延迟24小时的数量");
        AuditDelayCount auditDelayCount = auditDelayCountService.getByUserCode(userCode);
        if(auditDelayCount == null) {
            logger.error("查询失败");
            jsonObject.put(Constant.code, 200);
            jsonObject.put(Constant.success, false);
            jsonObject.put(Constant.data, null);
            jsonObject.put(Constant.msg, "查询失败");
            return jsonObject.toString();
        } else {
            jsonObject.put(Constant.code, 200);
            jsonObject.put(Constant.success, true);
            jsonObject.put(Constant.data, auditDelayCount);
            jsonObject.put(Constant.msg, "查询成功");
            return jsonObject.toString();
        }
    }
}
