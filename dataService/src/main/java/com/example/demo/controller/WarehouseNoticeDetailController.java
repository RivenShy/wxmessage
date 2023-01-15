package com.example.demo.controller;

import com.example.demo.entity.WarehouseNotice;
import com.example.demo.entity.WarehouseNoticeDetail;
import com.example.demo.result.R;
import com.example.demo.service.IWarehouseNoticeDetailService;
import com.example.demo.service.IWarehouseNoticeService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/warehouseNoticeDetail")
public class WarehouseNoticeDetailController {

    private static Logger logger = Logger.getLogger(AuditDelayCountController.class);

    @Autowired
    private IWarehouseNoticeDetailService warehouseNoticeDetailService;

    @GetMapping("/getWarehouseNoticeDetailByDocEntry/{docEntry}")
    public R getWarehouseNoticeDetailByDocEntry(@PathVariable String docEntry) {

        List<WarehouseNoticeDetail> warehouseNoticeDetailList = warehouseNoticeDetailService.getWarehouseNoticeDetailByDocEntry(docEntry);
        if(warehouseNoticeDetailList == null) {
            logger.error("查询入库详情信息失败");
            return R.fail("查询入库详情信息失败");
        }
        return R.data(warehouseNoticeDetailList);
    }
}
