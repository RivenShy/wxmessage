package com.example.demo.controller;

import com.example.demo.entity.WarehouseNotice;
import com.example.demo.result.R;
import com.example.demo.service.IWarehouseNoticeService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/warehouseNotice")
public class WarehouseNoticeController {

    private static Logger logger = Logger.getLogger(AuditDelayCountController.class);

    @Autowired
    private IWarehouseNoticeService warehouseNoticeService;

    @GetMapping("/getWarehouseNoticeByDocEntry/{docEntry}")
    public R getWarehouseNoticeByDocEntry(@PathVariable String docEntry) {

        WarehouseNotice warehouseNotice = warehouseNoticeService.getWarehouseNoticeByDocEntry(docEntry);
        if(warehouseNotice == null) {
            logger.error("查询入库通知信息失败");
            return R.fail("查询入库通知信息失败");
        }
        return R.data(warehouseNotice);
    }
}
