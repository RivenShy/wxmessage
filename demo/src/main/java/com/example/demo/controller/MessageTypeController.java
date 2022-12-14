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
        jsonObjectReturn.put(Constant.msg, "????????????");
        return jsonObjectReturn.toString();
    }

    @GetMapping("/deleteScheduleJob/{id}")
    public String deleteScheduleJob(@PathVariable("id") int id) {
        JSONObject jsonObjectReturn = new JSONObject();
        MessageType messageType = messageTypeService.get(id);
        if(messageType == null) {
            logger.error("???????????????????????????");
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "????????????");
        }
        boolean success = ScheduleConfig.deleteScheduleJob(id);
        if(success) {
            messageType.setStatus(1);
            int result = messageTypeService.updateStatus(messageType);
            if(result == 0) {
                logger.error("??????????????????????????????");
                jsonObjectReturn.put(Constant.code, 200);
                jsonObjectReturn.put(Constant.success, false);
                jsonObjectReturn.put(Constant.data, null);
                jsonObjectReturn.put(Constant.msg, "????????????");
            } else {
                jsonObjectReturn.put(Constant.code, 200);
                jsonObjectReturn.put(Constant.success, true);
                jsonObjectReturn.put(Constant.data, null);
                jsonObjectReturn.put(Constant.msg, "????????????");
            }
        } else {
            logger.error("????????????????????????");
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "????????????");
        }
        return jsonObjectReturn.toString();
    }

    @GetMapping("/addSchduleJob/{id}")
    public String addSchduleJob(@PathVariable("id") int id) {
        JSONObject jsonObjectReturn = new JSONObject();
        MessageType messageType = messageTypeService.get(id);
        if(messageType == null) {
            logger.error("???????????????????????????");
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "????????????");
            return jsonObjectReturn.toString();
        }
        boolean success = ScheduleConfig.addSchduleJob(messageType);
        if(success) {
            messageType.setStatus(0);
            int result = messageTypeService.updateStatus(messageType);
            if(result == 0) {
                logger.error("??????????????????????????????");
                jsonObjectReturn.put(Constant.code, 200);
                jsonObjectReturn.put(Constant.success, false);
                jsonObjectReturn.put(Constant.data, null);
                jsonObjectReturn.put(Constant.msg, "????????????");
            } else{
                jsonObjectReturn.put(Constant.code, 200);
                jsonObjectReturn.put(Constant.success, true);
                jsonObjectReturn.put(Constant.data, null);
                jsonObjectReturn.put(Constant.msg, "????????????");
            }
        } else {
            logger.error("????????????????????????");
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "????????????");
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
            logger.error("????????????");
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "????????????");
            return jsonObjectReturn.toString();
        }
        MessageType messageTypeDB = messageTypeService.get(id);
        if(messageTypeDB == null) {
            logger.error("????????????????????????");
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "????????????????????????");
            return jsonObjectReturn.toString();
        }
        if(messageTypeDB.getStatus() == 0) {
            logger.error("status???0???????????????????????????");
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "????????????????????????");
            return jsonObjectReturn.toString();
        }
        MessageType messageTypeArgs = new MessageType();
        messageTypeArgs.setId(id);
        messageTypeArgs.setScheduleTime(scheduleTime);
        int result = messageTypeService.updateScheduleTimeById(messageTypeArgs);
        if(result == 0) {
            logger.error("??????scheduleTime??????");
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "????????????");
            return jsonObjectReturn.toString();
        }
        jsonObjectReturn.put(Constant.code, 200);
        jsonObjectReturn.put(Constant.success, true);
        jsonObjectReturn.put(Constant.data, null);
        jsonObjectReturn.put(Constant.msg, "????????????");
        return jsonObjectReturn.toString();
    }

    @PostMapping("/add")
    public String add(@RequestBody MessageType messageType) {
//        String messageName = messageType.getMessageName();
        String scheduleTime = messageType.getScheduleTime();
        String messageTime = messageType.getMessageTime();
        JSONObject jsonObjectReturn = new JSONObject();
//        if(!messageName.equals(MessageType.enumMessageType.EMT_ProcessToApprove.getName())) {
//            logger.error("??????????????????????????????");
//            jsonObjectReturn.put(Constant.code, 200);
//            jsonObjectReturn.put(Constant.success, false);
//            jsonObjectReturn.put(Constant.data, null);
//            jsonObjectReturn.put(Constant.msg, "??????????????????????????????");
//            return jsonObjectReturn.toString();
//        }
        // ???????????????????????????????????????
        String messageName = MessageType.enumMessageType.EMT_ProcessToApprove.getName();
        if(StringUtil.isEmpty(messageTime) || !(messageTime.equals("??????") || messageTime.equals("??????"))) {
            logger.error("messageTime??????????????????");
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "messageTime??????????????????");
            return jsonObjectReturn.toString();
        }
        if(StringUtil.isEmpty(scheduleTime)) {
            logger.error("scheduleTime????????????");
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "scheduleTime????????????");
            return jsonObjectReturn.toString();
        }
        int serverId = messageType.getServerId();
        Server server = serverService.get(serverId);
        if(server == null) {
            logger.error("????????????????????????,serverId=" + serverId);
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "????????????????????????,serverId=" + serverId);
            return jsonObjectReturn.toString();
        }
//        Customer customer = customerService.get(server.getCustomerId());
//        if(customer == null) {
//            logger.error("?????????????????????,customerId=" + server.getCustomerId());
//            jsonObjectReturn.put(Constant.code, 200);
//            jsonObjectReturn.put(Constant.success, false);
//            jsonObjectReturn.put(Constant.data, null);
//            jsonObjectReturn.put(Constant.msg, "?????????????????????,customerId=" + server.getCustomerId());
//            return jsonObjectReturn.toString();
//        }

        MessageType messageTypeArgs = new MessageType();
        messageTypeArgs.setMessageName(messageName);
        messageTypeArgs.setScheduleTime(scheduleTime);
        messageTypeArgs.setServerId(serverId);
        // ??????????????????????????????????????????????????????????????????????????????userId???-1
        messageTypeArgs.setUserId(-1);
        messageTypeArgs.setDescription(messageType.getDescription());
        messageTypeArgs.setMessageTime(messageTime);
        // ???????????????????????????
        messageTypeArgs.setStatus(1);
        int result = messageTypeService.add(messageTypeArgs);
        if(result == 1) {
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, true);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "????????????");
            return jsonObjectReturn.toString();
        } else {
            logger.error("????????????????????????" + messageTypeArgs);
            jsonObjectReturn.put(Constant.code, 200);
            jsonObjectReturn.put(Constant.success, false);
            jsonObjectReturn.put(Constant.data, null);
            jsonObjectReturn.put(Constant.msg, "????????????????????????");
            return jsonObjectReturn.toString();
        }
    }
}
