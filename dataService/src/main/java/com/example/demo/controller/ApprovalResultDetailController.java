package com.example.demo.controller;

import com.example.demo.entity.ApprovalResultDetail;
import com.example.demo.result.R;
import com.example.demo.service.IApprovalResultDetailService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/approvalResultDetail")
public class ApprovalResultDetailController {
    private static Logger logger = Logger.getLogger(ApprovalResultDetailController.class);

    @Autowired
    private IApprovalResultDetailService approvalResultDetailService;

    @GetMapping("/getByCodeId/{codeId}")
    public R getByCodeId(@PathVariable String codeId) {
        List<ApprovalResultDetail> approvalResultDetailList = approvalResultDetailService.getByCodeId(codeId);
        if(approvalResultDetailList == null) {
            logger.error("查询审批结果详情信息失败");
            return R.fail("查询审批结果详情信息失败");
        }
        return R.data(approvalResultDetailList);
    }
}
