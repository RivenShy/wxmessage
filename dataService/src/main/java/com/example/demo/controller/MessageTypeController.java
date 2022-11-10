package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.Message;
import com.example.demo.entity.MessageType;
import com.example.demo.service.MessageService;
import com.example.demo.service.MessageTypeService;
import com.example.demo.util.Constant;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/messageType")
public class MessageTypeController {

    @Autowired
    private MessageTypeService messageTypeService;

    @GetMapping("/listMessageType")
    public ModelAndView listUserInfo() {
        return new ModelAndView("listMessageType");
    }

    @GetMapping("/messageTypes")
    public PageInfo<MessageType> list(@RequestParam(value = "start", defaultValue = "1") int start, @RequestParam(value = "size", defaultValue = "5") int size) throws Exception {
        PageHelper.startPage(start,size,"id desc");
        List<MessageType> hs = messageTypeService.list();
//        System.out.println(hs.size());

        PageInfo<MessageType> page = new PageInfo<>(hs,5);

        return page;
    }

    @GetMapping("/list")
    public String list() {
        List<MessageType> msgTypeList = messageTypeService.list();
        JSONObject jsonObjectReturn = new JSONObject();
        jsonObjectReturn.put(Constant.code, 200);
        jsonObjectReturn.put(Constant.success, true);
        jsonObjectReturn.put(Constant.data, msgTypeList);
        jsonObjectReturn.put(Constant.msg, "操作成功");
        return jsonObjectReturn.toString();
    }
}
