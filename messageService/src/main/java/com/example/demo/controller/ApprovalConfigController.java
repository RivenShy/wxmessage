package com.example.demo.controller;

import com.example.demo.entity.ApprovalConfig;
import com.example.demo.result.R;
import com.example.demo.service.IApprovalConfigService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/approvalConfig")
public class ApprovalConfigController {
    private static Logger logger = Logger.getLogger(ApprovalConfigController.class);

    @Autowired
    private IApprovalConfigService approvalConfigService;

    @GetMapping("/list")
    public R list() {
        List<ApprovalConfig> approvalConfigList = approvalConfigService.list();
        if(approvalConfigList == null) {
            logger.error("查询审批配置表失败");
            return R.fail("查询审批配置表失败");
        }
        return R.data(approvalConfigList);
    }

    @GetMapping("/getByAuditName/{auditName}")
    public R getByAuditName(@PathVariable String auditName) {
        ApprovalConfig approvalConfig = approvalConfigService.selectByAuditName(auditName);
        if(approvalConfig == null) {
            logger.error("查询审批配置信息失败");
            return R.fail("查询审批配置信息失败");
        }
        return R.data(approvalConfig);
    }
}
