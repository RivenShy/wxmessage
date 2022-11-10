package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.Message;
import com.example.demo.entity.UserInfo;
import com.example.demo.service.MessageService;
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
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/listMessage")
    public ModelAndView listUserInfo() {
        return new ModelAndView("listMessage");
    }

    @GetMapping("/messages")
    public PageInfo<Message> list(@RequestParam(value = "start", defaultValue = "1") int start, @RequestParam(value = "size", defaultValue = "5") int size) throws Exception {
        PageHelper.startPage(start,size,"id desc");
        List<Message> hs = messageService.list();
//        System.out.println(hs.size());

        PageInfo<Message> page = new PageInfo<>(hs,5);

        return page;
    }

//    @GetMapping("/list")
//    public String list() {
//        List<Message> msgList = messageService.list();
//        JSONObject jsonObjectReturn = new JSONObject();
//        jsonObjectReturn.put(Constant.code, 200);
//        jsonObjectReturn.put(Constant.success, true);
//        jsonObjectReturn.put(Constant.data, msgList);
//        jsonObjectReturn.put(Constant.msg, "操作成功");
//        return jsonObjectReturn.toString();
//    }
}
