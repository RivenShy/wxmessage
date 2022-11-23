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
        logger.info("查询待审批数量和延迟24小时的数量");
        List<AuditDelayCount> list = auditDelayCountService.list();
        if(list == null) {
            jsonObject.put(Constant.code, 200);
            jsonObject.put(Constant.success, true);
            jsonObject.put(Constant.data, list);
            jsonObject.put(Constant.msg, "查询失败");
            return jsonObject.toString();
        } else {
            jsonObject.put(Constant.code, 200);
            jsonObject.put(Constant.success, false);
            jsonObject.put(Constant.data, list);
            jsonObject.put(Constant.msg, "查询成功");
            return jsonObject.toString();
        }
    }
}
