package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.Message;
import com.example.demo.entity.UserInfo;
import com.example.demo.result.R;
import com.example.demo.service.MessageService;
import com.example.demo.util.Constant;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {

    private static Logger logger = Logger.getLogger(MessageController.class);

    @Autowired
    private MessageService messageService;

    @GetMapping("/listMessage")
    public ModelAndView listUserInfo() {
        return new ModelAndView("listMessage");
    }

    @GetMapping("/listMessageTest")
    public ModelAndView listMessageTest() {
        return new ModelAndView("listMessageTest");
    }

    @GetMapping("/messages")
    public PageInfo<Message> list(@RequestParam(value = "start", defaultValue = "1") int start, @RequestParam(value = "size", defaultValue = "5") int size, @RequestParam(value = "deleted", defaultValue = "0") int deleted) throws Exception {
        PageHelper.startPage(start,size,"id desc");
        List<Message> hs = messageService.list(deleted);
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

    @PostMapping("/listCondition")
    public PageInfo<Message> listCondition(@RequestBody Message message) throws Exception {
        logger.info(message);
        PageHelper.startPage(message.getPageNum(), message.getPageSize(),"id desc");
        List<Message> hs = messageService.listCondition(message);
//        System.out.println(hs.size());
//        for(Message message : hs) {
//            System.out.println("sendTime = " + message.getSendTime());
//        }
        PageInfo<Message> page = new PageInfo<>(hs,5);

        return page;
    }

    @PostMapping("/remove")
    public R<Boolean> remove(@RequestParam Integer id) {
        if(id == null) {
            logger.error("id为null");
            return R.fail("id不能为空");
        }
        int result = messageService.removeById(id);
        return R.status(result != 0);
    }
}
