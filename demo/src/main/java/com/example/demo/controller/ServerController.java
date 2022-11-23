package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.Customer;
import com.example.demo.entity.Message;
import com.example.demo.entity.Server;
import com.example.demo.service.ServerService;
import com.example.demo.util.Constant;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/server")
public class ServerController {

    @Autowired
    private ServerService serverService;

    private static Logger logger = Logger.getLogger(ServerController.class);

    @GetMapping("/listServer")
    public ModelAndView listUserInfo() {
        return new ModelAndView("listServer");
    }

    @GetMapping("/servers")
    public PageInfo<Server> list(@RequestParam(value = "start", defaultValue = "1") int start, @RequestParam(value = "size", defaultValue = "5") int size) throws Exception {
        PageHelper.startPage(start,size,"id desc");
        List<Server> hs = serverService.list();
//        System.out.println(hs.size());
//        for(Message message : hs) {
//            System.out.println("sendTime = " + message.getSendTime());
//        }
        PageInfo<Server> page = new PageInfo<>(hs,5);

        return page;
    }

    @PostMapping("/add")
    public String add(@RequestBody Server server) {
        logger.info(server);
        JSONObject jsonObjectReturn = new JSONObject();
        if(StringUtil.isEmpty(server.getServerName()) || StringUtil.isEmpty(server.getServerIp()) || server.getCustomerId() == 0) {
            logger.error("参数错误");
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "参数错误");
        }
        int result = serverService.add(server);
        if(result == 0) {
            logger.error("result = " + result);
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "添加失败");
            return jsonObjectReturn.toString();
        }
        jsonObjectReturn.put(Constant.code, 200);
        jsonObjectReturn.put(Constant.success, true);
        jsonObjectReturn.put(Constant.data, null);
        jsonObjectReturn.put(Constant.msg, "操作成功");
        return jsonObjectReturn.toString();
    }

    @PostMapping("/update")
    public String update(@RequestBody Server server) {
        logger.info(server);
        JSONObject jsonObjectReturn = new JSONObject();
        if(StringUtil.isEmpty(server.getServerName()) || StringUtil.isEmpty(server.getServerIp()) || server.getCustomerId() == 0 || server.getId() == 0) {
            logger.error("参数错误");
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "参数错误");
        }
        int result = serverService.update(server);
        if(result == 0) {
            logger.error("result = " + result);
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "更新失败");
            return jsonObjectReturn.toString();
        }
        jsonObjectReturn.put(Constant.code, 200);
        jsonObjectReturn.put(Constant.success, true);
        jsonObjectReturn.put(Constant.data, null);
        jsonObjectReturn.put(Constant.msg, "操作成功");
        return jsonObjectReturn.toString();
    }

    @GetMapping("/list")
    public String list() {
        List<Server> serverList = serverService.list();
        JSONObject jsonObjectReturn = new JSONObject();
        if(serverService == null) {
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "查询失败");
        } else {
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, serverList);
            jsonObjectReturn.put(Constant.msg, "查询成功");
        }
        return jsonObjectReturn.toString();
    }
}
