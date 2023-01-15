package com.example.demo.controller;

import com.example.demo.entity.Server;
import com.example.demo.result.R;
import com.example.demo.service.ServerService;
import com.example.demo.util.OkHttpUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/materialHistory")
public class MaterialHistoryController {

    private static Logger logger = Logger.getLogger(MaterialHistoryController.class);

    @Autowired
    private ServerService serverService;

    @GetMapping("/getMeterialHistoryPriceByCondition")
    public R getMeterialHistoryPriceByCondition(@RequestParam(value = "start", defaultValue = "1") int start, @RequestParam(value = "size", defaultValue = "5") int size, @RequestParam(value = "queryParam",defaultValue = "") String queryParam, @RequestParam(value = "serverId") int serverId) {
        Server server = serverService.get(serverId);
        Map<String, String> map = new HashMap<>();
        map.put("start", String.valueOf(start));
        map.put("size", String.valueOf(size));
        map.put("queryParam", queryParam);
        map.put("size", String.valueOf(size));
        return OkHttpUtil.getMeterialHistoryPriceByCondition(server.getServerUrl(), map);
    }
}
