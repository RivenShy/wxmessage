package com.example.demo.controller;

import com.example.demo.entity.ApprovalTimeout;
import com.example.demo.result.R;
import com.example.demo.service.IApprovalTimeoutService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/approvalTimeout")
public class ApprovalTimeoutController {

    private static Logger logger = Logger.getLogger(ApprovalTimeoutController.class);

    @Autowired
    private IApprovalTimeoutService approvalTimeoutService;

    @GetMapping("/list")
    public R list() {
        List<ApprovalTimeout> approvalTimeoutList = approvalTimeoutService.list();
        if(approvalTimeoutList == null) {
            logger.error("查询审批超时信息失败");
            return R.fail("查询审批超时信息失败");
        }
        return R.data(approvalTimeoutList);
    }
}
