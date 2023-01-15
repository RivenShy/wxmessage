package com.example.demo.controller;

import com.example.demo.entity.ApprovalResult;
import com.example.demo.result.R;
import com.example.demo.service.IApprovalResultService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/approvalResult")
public class ApprovalResultController {
    private static Logger logger = Logger.getLogger(ApprovalResultController.class);

    @Autowired
    private IApprovalResultService approvalResultService;

    @GetMapping("/list")
    public R list() {
        List<ApprovalResult> approvalResultList = approvalResultService.list();
        if(approvalResultList == null) {
            logger.error("查询审批结果信息失败");
            return R.fail("查询审批结果信息失败");
        }
        return R.data(approvalResultList);
    }
}
