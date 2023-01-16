package com.example.demo.controller;

import com.example.demo.entity.MaterialHistory;
import com.example.demo.result.R;
import com.example.demo.service.IMaterialHistoryService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/materialHistory")
public class MaterialHistoryController {

    private static Logger logger = Logger.getLogger(MaterialHistoryController.class);

    @Autowired
    private IMaterialHistoryService materialHistoryService;

//    @GetMapping(value={"/getMeterialHistoryPriceByCondition/{queryParam}","/getMeterialHistoryPriceByCondition"})
//    public R getMeterialHistoryPriceByCondition(@PathVariable(value = "queryParam",required=false) String queryParam) {
//        List<MaterialHistory> materialHistoryList = materialHistoryService.selectMeterialHistoryPriceByCondition(queryParam);
//        if(materialHistoryList == null) {
//            logger.error("查询物料的历史采购价格失败");
//            return R.fail("查询物料的历史采购价格");
//        }
//        return R.data(materialHistoryList);
//    }

    @GetMapping("/getMeterialHistoryPriceByCondition")
    public R getMeterialHistoryPriceByCondition(@RequestParam(value = "start", defaultValue = "1") int start, @RequestParam(value = "size", defaultValue = "5") int size, @RequestParam(value = "queryParam",defaultValue = "") String queryParam) {
        PageHelper.startPage(start,size,"ItemCode desc");
        List<MaterialHistory> materialHistoryList = materialHistoryService.selectMeterialHistoryPriceByCondition(queryParam);
        if(materialHistoryList == null) {
            logger.error("查询物料的历史采购价格失败");
            return R.fail("查询物料的历史采购价格");
        }
        PageInfo<MaterialHistory> page = new PageInfo<>(materialHistoryList,5);
        return R.data(page);
    }
}
