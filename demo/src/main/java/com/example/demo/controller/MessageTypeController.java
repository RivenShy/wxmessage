package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.config.ScheduleConfig;
import com.example.demo.entity.Customer;
import com.example.demo.entity.Message;
import com.example.demo.entity.MessageType;
import com.example.demo.entity.Server;
import com.example.demo.service.CustomerService;
import com.example.demo.service.MessageService;
import com.example.demo.service.MessageTypeService;
import com.example.demo.service.ServerService;
import com.example.demo.util.Constant;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.StringUtils;

import java.util.List;

@RestController
@RequestMapping("/messageType")
public class MessageTypeController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ServerService serverService;

    private static Logger logger = Logger.getLogger(MessageTypeController.class);

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

    @GetMapping("/deleteScheduleJob/{id}")
    public String deleteScheduleJob(@PathVariable("id") int id) {
        JSONObject jsonObjectReturn = new JSONObject();
        MessageType messageType = messageTypeService.get(id);
        if(messageType == null) {
            logger.error("查询不到该消息类型");
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "操作失败");
        }
        boolean success = ScheduleConfig.deleteScheduleJob(id);
        if(success) {
            messageType.setStatus(1);
            int result = messageTypeService.updateStatus(messageType);
            if(result == 0) {
                logger.error("更新消息类型状态失败");
                jsonObjectReturn.put(Constant.code, 200);
                jsonObjectReturn.put(Constant.success, false);
                jsonObjectReturn.put(Constant.data, null);
                jsonObjectReturn.put(Constant.msg, "操作失败");
            } else {
                jsonObjectReturn.put(Constant.code, 200);
                jsonObjectReturn.put(Constant.success, true);
                jsonObjectReturn.put(Constant.data, null);
                jsonObjectReturn.put(Constant.msg, "操作成功");
            }
        } else {
            logger.error("删除定时任务失败");
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "操作失败");
        }
        return jsonObjectReturn.toString();
    }

    @GetMapping("/addSchduleJob/{id}")
    public String addSchduleJob(@PathVariable("id") int id) {
        JSONObject jsonObjectReturn = new JSONObject();
        MessageType messageType = messageTypeService.get(id);
        if(messageType == null) {
            logger.error("查询不到该消息类型");
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "操作失败");
            return jsonObjectReturn.toString();
        }
        boolean success = ScheduleConfig.addSchduleJob(messageType);
        if(success) {
            messageType.setStatus(0);
            int result = messageTypeService.updateStatus(messageType);
            if(result == 0) {
                logger.error("更新消息类型状态失败");
                jsonObjectReturn.put(Constant.code, 200);
                jsonObjectReturn.put(Constant.success, false);
                jsonObjectReturn.put(Constant.data, null);
                jsonObjectReturn.put(Constant.msg, "操作失败");
            } else{
                jsonObjectReturn.put(Constant.code, 200);
                jsonObjectReturn.put(Constant.success, true);
                jsonObjectReturn.put(Constant.data, null);
                jsonObjectReturn.put(Constant.msg, "操作成功");
            }
        } else {
            logger.error("添加定时任务失败");
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "操作失败");
        }
        return jsonObjectReturn.toString();
    }

    @PostMapping("/updateScheduleTime")
    public String updateScheduleTime(@RequestBody MessageType messageType) {
//        @RequestParam Integer id, @RequestParam String scheduleTime;
        int id = messageType.getId();
        String scheduleTime = messageType.getScheduleTime();
        JSONObject jsonObjectReturn = new JSONObject();
        if(id == 0 || StringUtil.isEmpty(scheduleTime)) {
            logger.error("参数错误");
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "参数错误");
            return jsonObjectReturn.toString();
        }
        MessageType messageTypeDB = messageTypeService.get(id);
        if(messageTypeDB == null) {
            logger.error("找不到该消息类型");
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "找不到该消息类型");
            return jsonObjectReturn.toString();
        }
        if(messageTypeDB.getStatus() == 0) {
            logger.error("status为0，定时任务在运行中");
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "请先停用定时任务");
            return jsonObjectReturn.toString();
        }
        MessageType messageTypeArgs = new MessageType();
        messageTypeArgs.setId(id);
        messageTypeArgs.setScheduleTime(scheduleTime);
        int result = messageTypeService.updateScheduleTimeById(messageTypeArgs);
        if(result == 0) {
            logger.error("更新scheduleTime失败");
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "参数错误");
            return jsonObjectReturn.toString();
        }
        jsonObjectReturn.put(Constant.code, 200);
        jsonObjectReturn.put(Constant.success, true);
        jsonObjectReturn.put(Constant.data, null);
        jsonObjectReturn.put(Constant.msg, "操作成功");
        return jsonObjectReturn.toString();
    }

    @PostMapping("/add")
    public String add(@RequestBody MessageType messageType) {
//        String messageName = messageType.getMessageName();
        String scheduleTime = messageType.getScheduleTime();
        String messageTime = messageType.getMessageTime();
        JSONObject jsonObjectReturn = new JSONObject();
//        if(!messageName.equals(MessageType.enumMessageType.EMT_ProcessToApprove.getName())) {
//            logger.error("不存在该消息模板名称");
//            jsonObjectReturn.put(Constant.code, 200);
//            jsonObjectReturn.put(Constant.success, false);
//            jsonObjectReturn.put(Constant.data, null);
//            jsonObjectReturn.put(Constant.msg, "不存在该消息模板名称");
//            return jsonObjectReturn.toString();
//        }
        // 目前只有流程待审批消息模板
        String messageName = MessageType.enumMessageType.EMT_ProcessToApprove.getName();
        if(StringUtil.isEmpty(messageTime) || !(messageTime.equals("早上") || messageTime.equals("晚上"))) {
            logger.error("messageTime时间段不正确");
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "messageTime时间段不正确");
            return jsonObjectReturn.toString();
        }
        if(StringUtil.isEmpty(scheduleTime)) {
            logger.error("scheduleTime不能为空");
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "scheduleTime不能为空");
            return jsonObjectReturn.toString();
        }
        int serverId = messageType.getServerId();
        Server server = serverService.get(serverId);
        if(server == null) {
            logger.error("找不到服务器信息,serverId=" + serverId);
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "找不到服务器信息,serverId=" + serverId);
            return jsonObjectReturn.toString();
        }
//        Customer customer = customerService.get(server.getCustomerId());
//        if(customer == null) {
//            logger.error("找不到客户信息,customerId=" + server.getCustomerId());
//            jsonObjectReturn.put(Constant.code, 200);
//            jsonObjectReturn.put(Constant.success, false);
//            jsonObjectReturn.put(Constant.data, null);
//            jsonObjectReturn.put(Constant.msg, "找不到客户信息,customerId=" + server.getCustomerId());
//            return jsonObjectReturn.toString();
//        }

        MessageType messageTypeArgs = new MessageType();
        messageTypeArgs.setMessageName(messageName);
        messageTypeArgs.setScheduleTime(scheduleTime);
        messageTypeArgs.setServerId(serverId);
        // 目前都是针对客户的所有用户，有待审核数据的用户，所以userId为-1
        messageTypeArgs.setUserId(-1);
        messageTypeArgs.setDescription(messageType.getDescription());
        messageTypeArgs.setMessageTime(messageTime);
        // 新建默认是停用状态
        messageTypeArgs.setStatus(1);
        int result = messageTypeService.add(messageTypeArgs);
        if(result == 1) {
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, true);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "操作成功");
            return jsonObjectReturn.toString();
        } else {
            logger.error("增加消息类型失败" + messageTypeArgs);
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "增加消息类型失败");
            return jsonObjectReturn.toString();
        }
    }
}
