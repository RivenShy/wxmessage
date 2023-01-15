package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.config.ScheduleConfig;
import com.example.demo.entity.*;
import com.example.demo.result.R;
import com.example.demo.service.*;
import com.example.demo.util.InvokeSetGetUtil;
import com.example.demo.util.OkHttpUtil;
import com.example.demo.util.WxUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/messageTemplate")
public class MessageTemplateController {

    private static Logger logger = Logger.getLogger(MessageTypeController.class);

    /**
     * redis键前缀，判断重复发送时，区分不同的推送消息
     */
    private static final String ApprovalTimeout_Redis_Prefix = "approvalTimeout-";

    /**
     * redis键前缀，判断重复发送时，区分不同的推送消息
     */
    private static final String ApprovalResult_Redis_Prefix = "approvalResult-";

    @Autowired
    private ServerService serverService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private IMessageTemplateService messageTemplateService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private IConsultantService consultantService;

    @GetMapping("/messageTemplates")
    public R list(@RequestParam(value = "start", defaultValue = "1") int start, @RequestParam(value = "size", defaultValue = "5") int size, @RequestParam(value = "deleted", defaultValue = "0") int deleted) throws Exception {
        PageHelper.startPage(start,size,"id desc");
        List<MessageTemplate> messageTemplateList = messageTemplateService.list(deleted);
        if(messageTemplateList == null) {
            logger.error("查询消息内容模板失败");
            return R.fail("查询消息内容模板失败");
        }
        PageInfo<MessageTemplate> page = new PageInfo<>(messageTemplateList,5);

        return R.data(page);
    }

    @PostMapping("/update")
    public R update(@RequestBody MessageTemplate messageTemplate) {
        if(StringUtil.isEmpty(messageTemplate.getTemplateName()) || StringUtil.isEmpty(messageTemplate.getWxTemplateId()) ||
           StringUtil.isEmpty(messageTemplate.getFirstData())) {
            return R.fail("参数错误");
        }
        int result = messageTemplateService.update(messageTemplate);
        if(result == 0) {
            logger.error("更新推送消息内容模板失败");
            return R.fail("更新失败");
        }
        return R.success("操作成功");
    }

    @PostMapping("/delete/{id}")
    public R delete(@PathVariable int id) {
        int result = messageTemplateService.delete(id);
        if(result == 0) {
            logger.error("删除推送消息内容模板失败");
            return R.fail("删除失败");
        }
        return R.success("操作成功");
    }

    @PostMapping("/add")
    public R add(@RequestBody MessageTemplate messageTemplate) {
        if(StringUtil.isEmpty(messageTemplate.getTemplateName()) || StringUtil.isEmpty(messageTemplate.getWxTemplateId()) ||
                StringUtil.isEmpty(messageTemplate.getFirstData())) {
            return R.fail("参数错误");
        }
        int result = messageTemplateService.add(messageTemplate);
        if(result == 0) {
            logger.error("增加推送消息内容模板失败");
            return R.fail("增加推送消息内容模板失败");
        }
        return R.success("操作成功");
    }

    // 通用推送消息接口
    @PostMapping("/commonSendMessage")
    public R commonSendMessage(@RequestBody MessageTemplate messageTemplate) {
        // 消息类型
        // 推送消息的人、角色
        // 推送的内容 dataService
//        比如待审批
        return handleCommonSendMessage(messageTemplate);
    }

    public static R handleCommonSendMessage(MessageTemplate messageTemplateArgs) {
        MessageTemplate messageTemplateDB = ScheduleConfig.getMessageTemplateService().get(messageTemplateArgs.getId());
        if(messageTemplateDB == null) {
            logger.error("查询消息模板失败");
            return R.fail("查询消息模板失败");
        }
        Server server = ScheduleConfig.getServerService().getServerByServerIp(messageTemplateArgs.getServerIp());
        if(server == null) {
            logger.error("查询服务器信息失败");
            return R.fail("查询服务器信息失败");
        }
        if(MessageTemplate.enumMessageTemplateType.EMTT_ProcessToApproveNoticeMorning.getIndex() == messageTemplateDB.getId()) {
            return handleSendProcessToApproveNoticeMorning(server, messageTemplateDB);
        } else if(MessageTemplate.enumMessageTemplateType.EMTT_ProcessToApproveNoticeNight.getIndex() == messageTemplateDB.getId()) {
            return handleSendProcessToApproveNoticeNight(server, messageTemplateDB.getMessageTime(), messageTemplateDB);

        } else if(MessageTemplate.enumMessageTemplateType.EMTT_WarehousingNotice.getIndex() == messageTemplateDB.getId()) {
            //
            messageTemplateDB.setMasterTableUniqueKey(messageTemplateArgs.getMasterTableUniqueKey());
            return handleSendWarehousingNotice(server, messageTemplateDB);
        } else if(MessageTemplate.enumMessageTemplateType.EMTT_ApprovalPass.getIndex() == messageTemplateDB.getId()
              || (MessageTemplate.enumMessageTemplateType.EMTT_ApprovalReturn.getIndex() == messageTemplateDB.getId())) {
            return handleSendApprovalResult(server, messageTemplateDB);
        }  else if(MessageTemplate.enumMessageTemplateType.EMTT_ApprovalTimeout.getIndex() == messageTemplateDB.getId()) {
            return handleSendApprovalTimeout(server, messageTemplateDB);
        }
        else {
            logger.error("未知的模板消息类型");
            R.fail("未知的模板消息类型");
        }
        return null;
    }

    /**
     * 发送审批超时通知消息
     */
    private static R handleSendApprovalTimeout(Server server, MessageTemplate messageTemplate) {
        String messageName = messageTemplate.getTemplateName();
        Customer customer = ScheduleConfig.getCustomerService().get(server.getCustomerId());
        // 查询审批超时数据
        List<ApprovalTimeout> approvalTimeoutList = OkHttpUtil.getApprovalTimeout(server.getServerUrl(), null);
        if (approvalTimeoutList == null) {
            logger.error("查询审批超时数据失败");
            return R.fail("查询审批超时数据失败");
        }
        // forTest
//        List<ApprovalTimeout> approvalTimeoutList = new ArrayList<>();
//        ApprovalTimeout approvalTimeoutTest = new ApprovalTimeout();
//        approvalTimeoutTest.setCodeid("test11111111111");
//        approvalTimeoutTest.setSubTime(new Date());
//        approvalTimeoutTest.setAuditName("报销单");
//        approvalTimeoutTest.setLastAuditTime(new Date());
//        approvalTimeoutTest.setCodeDesc("CodeDescTest");
//        approvalTimeoutTest.setNumDesc("NumDescTest");
//        approvalTimeoutTest.setJobuser("chenq5");
//        approvalTimeoutList.add(approvalTimeoutTest);
//        approvalTimeout
        if(approvalTimeoutList.size() == 0) {
            logger.info("审批超时的单据数目为0，企业名称：" + customer.getCustomerName());
            return R.success("审批超时的单据数目为0，企业名称：" + customer.getCustomerName());
        }
        Boolean sendSuccess = false;
        for(ApprovalTimeout approvalTimeout : approvalTimeoutList) {
            // 判断是否已经发送过
            if(ScheduleConfig.getStringRedisTemplate().hasKey(ApprovalTimeout_Redis_Prefix + server.getServerIp() + approvalTimeout.getCodeid())) {
//                logger.info("已发送过该审批超时通知，codeId=" + approvalTimeout.getCodeid());
                continue;
            }
            UserInfo userInfoArgs = new UserInfo();
            userInfoArgs.setServerId(server.getId());
            userInfoArgs.setUserId(approvalTimeout.getJobuser());
            UserInfo userInfo = ScheduleConfig.getUserInfoService().getUserInfoByServerIdAndUserId(userInfoArgs);
            if(userInfo == null || userInfo.getOpenId() == null) {
                // 61服务器未有该用户信息或用户账号未绑定微信
                addUnbindUser(server, customer, approvalTimeout.getJobuser());
                continue;
            }
            ApprovalConfig approvalConfig = ScheduleConfig.getApprovalConfigService().selectByAuditName(approvalTimeout.getAuditName());
            if(approvalConfig == null) {
                logger.error("查询审批配置信息失败，auditName = " + approvalTimeout.getAuditName());
                continue;
            }
            Consultant consultant = ScheduleConfig.getConsultantService().get(userInfo.getConsultantId());
            // 发送消息前，先插入message表，这样才能得到消息Id，放到推送消息链接参数里
            Message message = new Message();
            message.setSendTime(new Date());
            message.setUserId(userInfo.getId());
            message.setMessageTemplateId(messageTemplate.getId());
            message.setMessageName("审批超时通知");
            // 默认失败，成功后再更新为0
            message.setStatus(1);
            int messageId = ScheduleConfig.getMessageService().add(message);
            if (messageId == 0) {
                logger.error("添加消息失败");
                return R.fail("添加消息失败");
            }
//            sendSuccess = WxUtil.sendApprovalTimeout(customer.getCustomerName(), userInfo, message.getId(), approvalTimeout, consultant, messageTemplate, approvalConfig);
            sendSuccess = WxUtil.sendCommonApprovalTimeout(customer.getCustomerName(), userInfo, message.getId(), approvalTimeout, consultant, messageTemplate, approvalConfig);
            if (!sendSuccess) {
                logger.error("发送" + messageName + "消息失败，userCode = " + userInfo.getUserId());
//                return R.fail("发送" + messageName + "消息失败");
            } else {
                logger.info("发送" + messageName + "消息成功");
//                message.setId(messageId);
                message.setStatus(0);
                int result = ScheduleConfig.getMessageService().updateStatus(message);
                if (result == 1) {
                    logger.info("更新消息状态成功");
                } else {
                    logger.error("更新消息状态失败");
                }
                ScheduleConfig.getStringRedisTemplate().opsForValue().set(ApprovalTimeout_Redis_Prefix + server.getServerIp() + approvalTimeout.getCodeid(),"codeId",24, TimeUnit.HOURS);
            }
        }
        return R.status(sendSuccess);
    }

    /**
     * 发送入库通知消息
     */
    private static R handleSendWarehousingNotice(Server server, MessageTemplate messageTemplate) {
        if(StringUtil.isEmpty(messageTemplate.getMasterTableUniqueKey())) {
            logger.error("入库docEntry不能为空");
            return R.fail("入库docEntry不能为空");
        }
        String messageName = messageTemplate.getTemplateName();
        Customer customer = ScheduleConfig.getCustomerService().get(server.getCustomerId());
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("docEntry", messageTemplate.getMasterTableUniqueKey());
        WarehouseNotice warehouseNotice = OkHttpUtil.getWarehouseNoticeByDocEntry(server.getServerUrl(), hashMap);
        if(warehouseNotice == null) {
            logger.error("查询入库信息失败");
            return R.fail("查询入库信息失败");
        }
        warehouseNotice.setDocEntry(messageTemplate.getMasterTableUniqueKey());
        UserInfo userInfoArgs = new UserInfo();
        userInfoArgs.setServerId(server.getId());
        userInfoArgs.setUserId(warehouseNotice.getStorePerId());
        UserInfo userInfo = ScheduleConfig.getUserInfoService().getUserInfoByServerIdAndUserId(userInfoArgs);
        if(userInfo == null || userInfo.getOpenId() == null) {
//            logger.error("查询用户信息失败,检查用户" + warehouseNotice.getStorePerId() + "是否已绑定微信");
            addUnbindUser(server, customer,warehouseNotice.getStorePerId());
            return R.fail("查询用户信息失败,检查用户" + warehouseNotice.getStorePerId() + "是否已绑定微信");
        }
        Consultant consultant = ScheduleConfig.getConsultantService().get(userInfo.getConsultantId());
        // 发送消息前，先插入message表，这样才能得到消息Id，放到推送消息链接参数里
        Message message = new Message();
        message.setSendTime(new Date());
        message.setUserId(userInfo.getId());
        message.setMessageTemplateId(messageTemplate.getId());
        message.setMessageName("入库通知");
        // 默认失败，成功后再更新为0
        message.setStatus(1);
        int messageId = ScheduleConfig.getMessageService().add(message);
        if (messageId == 0) {
            logger.error("添加消息失败");
            return R.fail("添加消息失败");
        }
        Boolean sendSuccess = WxUtil.sendWarehousingNotice(customer.getCustomerName(), userInfo, message.getId(), warehouseNotice, consultant, messageTemplate);
        if (!sendSuccess) {
            logger.error("发送" + messageName + "消息失败");
            return R.fail("发送" + messageName + "消息失败");
        } else {
            logger.info("发送" + messageName + "消息成功");
            message.setId(messageId);
            message.setStatus(0);
            int result = ScheduleConfig.getMessageService().updateStatus(message);
            if (result == 1) {
                logger.info("更新消息状态成功");
            } else {
                logger.error("更新消息状态失败");
            }
            return R.success("发送" + messageName + "消息成功");
        }
    }

    /**
     * 发送审核结果通知消息
     */
    private static R handleSendApprovalResult(Server server, MessageTemplate messageTemplate) {
        String messageName = messageTemplate.getTemplateName();
//        UserInfo userInfo = ScheduleConfig.getUserInfoService().get(19);
        Customer customer = ScheduleConfig.getCustomerService().get(server.getCustomerId());
        // 查询dataService
        List<ApprovalResult> approvalResultList = OkHttpUtil.getApprovalResultList(server.getServerUrl(), null);
        if(approvalResultList == null) {
            logger.error("查询审批结果信息失败");
            return R.fail("查询审批结果信息失败");
        }
        if(approvalResultList.size() == 0) {
            logger.info("最近24小时内审批通过、审批退回的单据数目为0，企业名称：" + customer.getCustomerName());
            return R.success("最近24小时内审批通过、审批退回的单据数目为0，企业名称：" + customer.getCustomerName());
        }
        // forTest
//        List<ApprovalResult> approvalResultList = new ArrayList<>();
//        ApprovalResult approvalResultTest = new ApprovalResult();
//        approvalResultTest.setCodeid("test11111111111");
//        approvalResultTest.setSubTime(new Date());
//        approvalResultTest.setAuditName("报销单");
//        approvalResultTest.setStatus("已否决");
//        approvalResultTest.setCodeDesc("CodeDescTest");
//        approvalResultTest.setNumDesc("NumDescTest");
//        approvalResultTest.setSubMitUser("10086");
//        approvalResultList.add(approvalResultTest);
        Boolean sendSuccess = false;
        for(ApprovalResult approvalResult : approvalResultList) {
            // 判断是否已经发送过
            if(ScheduleConfig.getStringRedisTemplate().hasKey(ApprovalResult_Redis_Prefix + server.getServerIp() + approvalResult.getCodeid())) {
//                logger.info("已发送过该审批结果通知，codeId=" + approvalResult.getCodeid());
                continue;
            }
            ApprovalConfig approvalConfig = ScheduleConfig.getApprovalConfigService().selectByAuditName(approvalResult.getAuditName());
            if(approvalConfig == null) {
                logger.error("查询审批配置信息失败，auditName = " + approvalResult.getAuditName());
                continue;
            }
            UserInfo userInfoArgs = new UserInfo();
            String userCode = approvalResult.getSubMitUser();
            userInfoArgs.setUserId(userCode);
            userInfoArgs.setServerId(server.getId());
            UserInfo userInfo = ScheduleConfig.getUserInfoService().getUserInfoByServerIdAndUserId(userInfoArgs);
            if(userInfo == null || userInfo.getOpenId() == null) {
                // 61服务器未有该用户信息或用户账号未绑定微信
                addUnbindUser(server, customer, userCode);
                continue;
            }
            // 发送消息前，先插入message表，这样才能得到消息Id，放到推送消息链接参数里
            Message message = new Message();
            message.setSendTime(new Date());
            message.setUserId(userInfo.getId());
            message.setMessageTemplateId(messageTemplate.getId());
            message.setMessageName("审核结果通知");
            // 默认失败，成功后再更新为0
            message.setStatus(1);
            int messageId = ScheduleConfig.getMessageService().add(message);
            if (messageId == 0) {
                logger.error("添加消息失败");
//            continue;
            }
            Consultant consultant = ScheduleConfig.getConsultantService().get(userInfo.getConsultantId());
            if(ApprovalResult.statusPass.equals(approvalResult.getStatus())) {
                messageTemplate = ScheduleConfig.getMessageTemplateService().get(MessageTemplate.enumMessageTemplateType.EMTT_ApprovalPass.getIndex());
                messageName = messageTemplate.getTemplateName();
                sendSuccess = WxUtil.sendApprovalPass(customer.getCustomerName(), userInfo, message.getId(), approvalResult, consultant, messageTemplate, approvalConfig);
            } else {
                messageTemplate = ScheduleConfig.getMessageTemplateService().get(MessageTemplate.enumMessageTemplateType.EMTT_ApprovalReturn.getIndex());
                messageName = messageTemplate.getTemplateName();
                Map<String, String> hashMap = new HashMap<>();
                hashMap.put("codeId", approvalResult.getCodeid());
                List<ApprovalResultDetail> approvalResultDetailList = OkHttpUtil.getApprovalResultDetailByCodeID(server.getServerUrl(), hashMap);
                if(approvalResultDetailList == null) {
                    logger.error("查询审批结果详情信息失败");
                    return R.fail("查询审批结果详情信息失败");
                }
                // 没有详情页，先在模板消息展示不通过的审核人、审核意见、审核时间
                for(ApprovalResultDetail approvalResultDetail : approvalResultDetailList) {
                    if(approvalResultDetail.getAuditTime() != null && approvalResultDetail.getAuditTime().getTime() == approvalResult.getAuditTime().getTime()) {
                        approvalResult.setUserName(approvalResultDetail.getUserName());
                        approvalResult.setAuditMemo(approvalResultDetail.getAuditMemo());
                    }
                }
                sendSuccess = WxUtil.sendApprovalReturn(customer.getCustomerName(), userInfo, message.getId(), approvalResult, consultant, messageTemplate, approvalConfig);
            }
            if (!sendSuccess) {
                logger.error("发送" + messageName + "消息失败");
            } else {
                logger.info("发送" + messageName + "消息成功");
//                message.setId(messageId);
                message.setStatus(0);
                int result = ScheduleConfig.getMessageService().updateStatus(message);
                if (result == 1) {
                    logger.info("更新消息状态成功");
                } else {
                    logger.error("更新消息状态失败");
                }
                ScheduleConfig.getStringRedisTemplate().opsForValue().set(ApprovalResult_Redis_Prefix + server.getServerIp() + approvalResult.getCodeid(),"codeId",24, TimeUnit.HOURS);
            }
        }
        return R.status(sendSuccess);
    }


    /**
     * 推送流程待审批提醒(早消息)
     */
    public static R handleSendProcessToApproveNoticeMorning(Server server, MessageTemplate messageTemplate) {
        //
        String messageName = messageTemplate.getTemplateName();
        if (server == null || server.getServerUrl() == null) {
            logger.error("服务器或服务器url为null");
            return R.fail("服务器或服务器url为null");
        }
        // 早上统计消息
        boolean sendSuccess = false;
//        List<AuditDelayCount> auditDelayCountList = OkHttpUtil.getAuditDelayCountList(server.getServerUrl());
//        if(auditDelayCountList == null) {
//            logger.error("auditDelayCountList 为 null");
//            return R.fail("查询待审、已审批信息失败");
//        }
        // todo for test
        List<AuditDelayCount> auditDelayCountList = new ArrayList<>();
        AuditDelayCount auditDelayCountTest = new AuditDelayCount();
        auditDelayCountTest.setJobuser("10086");
        auditDelayCountTest.setDateTest(new Date());
        auditDelayCountList.add(auditDelayCountTest);
        // 查询触发条件
        List<MessageTemplateTrigger> messageTemplateTriggerList = ScheduleConfig.getMessageTemplateTriggerService().listByMessageTemplateId(messageTemplate.getId());
        if(messageTemplateTriggerList == null) {
            logger.error("查询消息发送触发条件表失败");
            return R.fail("查询消息发送触发条件表失败");
        }
        Customer customer = ScheduleConfig.getCustomerById(server.getCustomerId());
        if (customer == null) {
            logger.error("查询客户信息失败，id = " + server.getCustomerId());
            return R.fail("查询客户信息失败");
        }
        for (AuditDelayCount auditDelayCount : auditDelayCountList) {
            String userCode = auditDelayCount.getJobuser();
            UserInfo userInfoArgs = new UserInfo();
            userInfoArgs.setUserId(userCode);
            userInfoArgs.setServerId(server.getId());
            UserInfo userInfo = ScheduleConfig.getUserInfoByServerIdAndUserId(userInfoArgs);
            if (userInfo == null || userInfo.getOpenId() == null) {
                // 61服务器未有该用户信息或用户账号未绑定微信
                addUnbindUser(server, customer, userCode);
                continue;
            }
            if (userInfo.getOpenId() != null) {
                // todo 缺少前端页面
                boolean checkTriggerCondition = booleanTriggerCondition(messageTemplateTriggerList, auditDelayCount);
                if(!checkTriggerCondition) {
                    logger.error("未到触发条件");
                    continue;
                }
                // 发送消息前，先插入message表，这样才能得到消息Id，放到推送消息链接参数里
                Message message = new Message();
                message.setSendTime(new Date());
                message.setUserId(userInfo.getId());
                message.setMessageTemplateId(messageTemplate.getId());
//                            message.setMsgTypeId(messageTypeId);
                message.setMessageName(MessageType.ProcessToApproveMessageTime_MorningMessageName);
                // 默认失败，成功后再更新为0
                message.setStatus(1);
                int messageId = ScheduleConfig.addMessage(message);
                if (messageId == 0) {
                    logger.error("添加消息失败");
                    continue;
                }
                Consultant consultant = null;
                if(userInfo.getConsultantId() != 0) {
                    consultant = ScheduleConfig.getConsultantByUserId(userInfo.getConsultantId());
                }
                sendSuccess = WxUtil.sendCommonProcessApprovalMsgToUser(customer.getCustomerName(), userInfo, messageId, auditDelayCount, consultant, messageTemplate);
                if (!sendSuccess) {
                    logger.error("发送" + messageName + "消息失败");
//                                return R.fail("发送微信推送消息失败");
                } else {
                    logger.info("发送" + messageName + "消息成功");
                    message.setId(messageId);
                    message.setStatus(0);
                    int result = ScheduleConfig.updateMessageStatus(message);
                    if (result == 1) {
                        logger.info("更新消息状态成功");
                    } else {
                        logger.error("更新消息状态失败");
                    }
                }
            } else {
                logger.error("账号为" + userInfo.getUserId() + "的用户的openId为null,无法推送微信消息");
            }
        }
        if(sendSuccess) {
            return R.success("发送微信推送消息成功");
        } else {
            return R.fail("发送微信推送消息失败");
        }
    }

    private static boolean booleanTriggerCondition(List<MessageTemplateTrigger> messageTemplateTriggerList, AuditDelayCount auditDelayCount) {
        for(MessageTemplateTrigger messageTemplateTrigger : messageTemplateTriggerList) {
            String triggerName = messageTemplateTrigger.getTriggerFieldName();
            triggerName = triggerName.replaceAll("\\{","");
            triggerName = triggerName.replaceAll("\\}","");
            Object objValue = InvokeSetGetUtil.getter(auditDelayCount, triggerName);
            Class methodReturnType = InvokeSetGetUtil.getMethodReturnType(auditDelayCount, triggerName);
            if (methodReturnType == null) {
                logger.error("没有该业务字段，请检查");
                return false;
            }
            if(methodReturnType.equals(int.class)) {
                int getValue = (int) objValue;
                if (messageTemplateTrigger.getConditionSymbol().equals(MessageTemplateTrigger.enumTriggerConditionSymbol.EMTT_Greater.getName())) {
                    if (getValue <= Integer.valueOf(messageTemplateTrigger.getThreshold())) {
                        logger.error(triggerName + "不满足条件");
                        return false;
                    }
                } else if (messageTemplateTrigger.getConditionSymbol().equals(MessageTemplateTrigger.enumTriggerConditionSymbol.EMTT_Equal.getName())) {
                    if (getValue != Integer.valueOf(messageTemplateTrigger.getThreshold())) {
                        logger.error(triggerName + "不满足条件");
                        return false;
                    }
                } else if (messageTemplateTrigger.getConditionSymbol().equals(MessageTemplateTrigger.enumTriggerConditionSymbol.EMTT_Less.getName())) {
                    if (getValue >= Integer.valueOf(messageTemplateTrigger.getThreshold())) {
                        logger.error(triggerName + "不满足条件");
                        return false;
                    }
                }
            } else if (methodReturnType.equals(String.class)){
                String getValue = (String) objValue;
                if (messageTemplateTrigger.getConditionSymbol().equals("等于")) {
                    if (!getValue.equals(messageTemplateTrigger.getThreshold())) {
                        logger.error(triggerName + "不满足条件");
                        return false;
                    }
                }
            } else if(methodReturnType.equals(Date.class)) {
                Date getValue = (Date) objValue;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date dateThreshold = simpleDateFormat.parse(messageTemplateTrigger.getThreshold());
                    if (messageTemplateTrigger.getConditionSymbol().equals(MessageTemplateTrigger.enumTriggerConditionSymbol.EMTT_Greater.getName())) {
                        if (!getValue.after(dateThreshold)) {
                            logger.error(triggerName + "不满足条件");
                            return false;
                        }
                    } else if (messageTemplateTrigger.getConditionSymbol().equals(MessageTemplateTrigger.enumTriggerConditionSymbol.EMTT_Less.getName())) {
                        if (!getValue.before(dateThreshold)) {
                            logger.error(triggerName + "不满足条件");
                            return false;
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    /**
     * 推送流程待审批提醒(晚消息)
     */
    public static R handleSendProcessToApproveNoticeNight(Server server, String messageTime, MessageTemplate messageTemplate) {
        //
        String messageName = messageTemplate.getTemplateName();
        if (server == null || server.getServerUrl() == null) {
            logger.error("服务器或服务器url为null");
            return R.fail("服务器或服务器url为null");
        }
        // 晚上统计消息
        boolean sendSuccess = false;
        List<PendingApproval> pendingApprovalList = OkHttpUtil.getPendingApprovalList(server.getServerUrl());
        if(pendingApprovalList == null) {
            logger.error("auditDelayCountList 为 null");
            return R.fail("查询待审、已审批信息失败");
        }
        // todo for test
//        List<PendingApproval> pendingApprovalList = new ArrayList<>();
//        PendingApproval pendingApprovalTest = new PendingApproval();
//        pendingApprovalTest.setJobuser("10086");
//        pendingApprovalTest.setTodayCount(1);
//        pendingApprovalList.add(pendingApprovalTest);
        Customer customer = ScheduleConfig.getCustomerById(server.getCustomerId());
        if (customer == null) {
            logger.error("查询客户信息失败，id = " + server.getCustomerId());
            return R.fail("查询客户信息失败");
        }
        for (PendingApproval pendingApproval : pendingApprovalList) {
            // 审核、未审都为0，跳过
            if(pendingApproval.getTodayCount() == 0 && pendingApproval.getAdcount() == 0) {
                continue;
            }
            String userCode = pendingApproval.getJobuser();
            UserInfo userInfoArgs = new UserInfo();
            userInfoArgs.setUserId(userCode);
            userInfoArgs.setServerId(server.getId());
            UserInfo userInfo = ScheduleConfig.getUserInfoByServerIdAndUserId(userInfoArgs);
            if (userInfo == null || userInfo.getOpenId() == null) {
                // 61服务器未有该用户信息或用户账号未绑定微信
                addUnbindUser(server, customer, userCode);
                continue;
            }
            if (userInfo.getOpenId() != null) {
                // 发送消息前，先插入message表，这样才能得到消息Id，放到推送消息链接参数里
                Message message = new Message();
                message.setSendTime(new Date());
                message.setUserId(userInfo.getId());
                message.setMessageTemplateId(messageTemplate.getId());
//                            message.setMsgTypeId(messageTypeId);
                message.setMessageName(MessageType.ProcessToApproveMessageTime_NightMessageName);
                // 默认失败，成功后再更新为0
                message.setStatus(1);
                int messageId = ScheduleConfig.addMessage(message);
                if (messageId == 0) {
                    logger.error("添加消息失败");
                    continue;
                }
                Consultant consultant = null;
                if(userInfo.getConsultantId() != 0) {
                    consultant = ScheduleConfig.getConsultantByUserId(userInfo.getConsultantId());
                }
                sendSuccess = WxUtil.sendCommonProcessApprovalMsgToUserAtNight(customer.getCustomerName(), userInfo, messageId, pendingApproval, consultant, messageTemplate);
                if (!sendSuccess) {
                    logger.error("发送" + messageName + "消息失败");
                } else {
                    logger.info("发送" + messageName + "消息成功");
                    message.setId(messageId);
                    message.setStatus(0);
                    int result = ScheduleConfig.updateMessageStatus(message);
                    if (result == 1) {
                        logger.info("更新消息状态成功");
                    } else {
                        logger.error("更新消息状态失败");
                    }
                }
            } else {
                logger.error("编码为" + userInfo.getUserId() + "的用户的openId为null,无法推送微信消息");
            }
        }
        if(sendSuccess) {
            return R.success("发送微信推送消息成功");
        } else {
            return R.fail("发送微信推送消息失败");
        }

    }

    /**
     * 流程待审批早、晚推送消息查看详情
     */
    @GetMapping("/getCommonMessageInfo/{messageId}")
    public R getCommonMessageInfo(@PathVariable int messageId) {
        return handleGetProcessToApproveNoticeInfo(messageId);
    }

    /**
     * 查看入库通知消息详情
     */
    @GetMapping("/getWarehouseNoticeMessageInfo/{messageId}/{detailDocEntry}")
    public R getWarehouseNoticeMessageInfo(@PathVariable int messageId, @PathVariable String detailDocEntry) {
        return handleGetWarehouseNoticeMessageInfo(messageId, detailDocEntry);
    }

    /**
     * 查看审批结果消息详情
     */
    @GetMapping("/getApprovalResultMessageInfo/{messageId}/{codeId}")
    public R getApprovalResultMessageInfo(@PathVariable int messageId, @PathVariable String codeId) {
        return handleGetApprovalResultMessageInfo(messageId, codeId);
    }

    /**
     * 流程待审批早、晚推送消息查看详情
     */
    public static R handleGetProcessToApproveNoticeInfo(int messageId) {
        Message message = ScheduleConfig.getMessageService().get(messageId);
        if(message == null) {
            return R.fail("查询消息信息失败");
        }
        MessageTemplate messageTemplate = ScheduleConfig.getMessageTemplateService().get(message.getMessageTemplateId());
        if(messageTemplate == null) {
            return R.fail("查询模板消息失败");
        }
        Message messageArgs = new Message();
        messageArgs.setId(message.getId());
        messageArgs.setClickTime(new Date());
        int result = ScheduleConfig.getMessageService().updateClickTime(messageArgs);
        if(result == 1) {
            logger.info("更新消息点击时间成功");
        } else {
            logger.error("更新消息点击时间失败");
            return R.fail("更新消息点击时间失败");
        }
        if(MessageTemplate.enumMessageTemplateType.EMTT_ProcessToApproveNoticeMorning.getIndex() == messageTemplate.getId()) {
            return handleGetProcessToApproveNoticeMorningInfo(message);
        } else if(MessageTemplate.enumMessageTemplateType.EMTT_ProcessToApproveNoticeNight.getIndex() == messageTemplate.getId()) {
            return handleGetProcessToApproveNoticeNightInfo(message);
        }
//        else if(MessageTemplate.enumMessageTemplateType.EMTT_WarehousingNotice.getIndex() == messageTemplate.getId()) {
//            return handleGetWarehousingNoticeInfo(message);
//
//        } else if(MessageTemplate.enumMessageTemplateType.EMTT_ApprovalResultNotice.getIndex() == messageTemplate.getId()) {
//
//        } else if(MessageTemplate.enumMessageTemplateType.EMTT_MaterialCodeRequest.getIndex() == messageTemplate.getId()) {
//
//        } else if(MessageTemplate.enumMessageTemplateType.EMTT_MaterialsToPurchased.getIndex() == messageTemplate.getId()) {
//
//        } else if(MessageTemplate.enumMessageTemplateType.EMTT_FinancialReceiptsAndPaymentsToProcessed.getIndex() == messageTemplate.getId()) {
//        }
        else {
            logger.error("未知的模板消息类型");
            R.fail("未知的模板消息类型");
        }

        return null;
    }

    /**
     * 流程待审批早推送消息查看详情
     */
    private static R handleGetProcessToApproveNoticeMorningInfo(Message message) {
        UserInfo userInfo = ScheduleConfig.getUserInfoService().get(message.getUserId());
        if(userInfo == null) {
            logger.error("查询用户信息失败，id=" + message.getUserId());
            R.fail("查询用户信息失败");
        }
        Server server = ScheduleConfig.getServerService().get(userInfo.getServerId());
        if(server == null || StringUtil.isEmpty(server.getServerUrl())) {
            logger.error("找不到服务器信息或服务器 serverUrl 为空，id=" + userInfo.getServerId());
            R.fail("找不到服务器信息或服务器 serverUrl 为空");
        }
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("userCode", userInfo.getUserId());
        List<PendingApprovalDetail> pendingApprovalDetail = OkHttpUtil.getPendingApprovalDetailList(server.getServerUrl(), hashMap);
        if(pendingApprovalDetail == null) {
            logger.error("查询待审批详情失败");
            R.fail("查询待审批详情失败");
        }
        return R.data(pendingApprovalDetail);
    }

    /**
     * 流程待审批晚推送消息查看详情
     */
    private static R handleGetProcessToApproveNoticeNightInfo(Message message) {
        UserInfo userInfo = ScheduleConfig.getUserInfoService().get(message.getUserId());
        if(userInfo == null) {
            logger.error("查询用户信息失败，id=" + message.getUserId());
            R.fail("查询用户信息失败");
        }
        Server server = ScheduleConfig.getServerService().get(userInfo.getServerId());
        if(server == null || StringUtil.isEmpty(server.getServerUrl())) {
            logger.error("找不到服务器信息或服务器 serverUrl 为空，id=" + userInfo.getServerId());
            R.fail("找不到服务器信息或服务器 serverUrl 为空");
        }
        String userCode = userInfo.getUserId();
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("userCode", userCode);
        // 用户今日已审、待审数目
        PendingApproval pendingApprovalAdTotalCount = OkHttpUtil.getAdTototalCountByUserCode(server.getServerUrl(), hashMap);
        if(pendingApprovalAdTotalCount == null) {
            logger.error("查询用户" + userCode + "今日已审、待审数目");
        }
        // 用户平均时效
        PendingApproval pendingApprovalAverageTime = OkHttpUtil.getAverageTimeByUserCode(server.getServerUrl(), hashMap);
        if(pendingApprovalAverageTime == null) {
            logger.error("查询用户" + userCode + "平均时效失败");
        }
        // 今日已审排行
        List<PendingApproval> pendingApprovalTotalApprovalRank = OkHttpUtil.getTotalApprovalRank(server.getServerUrl(), null);
        if(pendingApprovalTotalApprovalRank == null) {
            logger.error("查询今日审核数目排行失败");
        }
        JSONObject jsonObject = new JSONObject();
        // 用户今日已审、待审数目
        jsonObject.put("pendingApprovalAdTotalCount", pendingApprovalAdTotalCount);
        // 用户平均时效
        jsonObject.put("pendingApprovalAverageTime", pendingApprovalAverageTime);
        // 今日已审排行
        jsonObject.put("pendingApprovalTotalApprovalRank", pendingApprovalTotalApprovalRank);
        return R.data(jsonObject);
    }

    /**
     * 入库通知推送消息查看详情
     */
    private static R handleGetWarehouseNoticeMessageInfo(int messageId, String detailDocEntry) {
        Message message = ScheduleConfig.getMessageService().get(messageId);
        if(message == null) {
            return R.fail("查询消息信息失败");
        }
        Message messageArgs = new Message();
        messageArgs.setId(message.getId());
        messageArgs.setClickTime(new Date());
        int result = ScheduleConfig.getMessageService().updateClickTime(messageArgs);
        if(result == 1) {
            logger.info("更新消息点击时间成功");
        } else {
            logger.error("更新消息点击时间失败");
            return R.fail("更新消息点击时间失败");
        }
        UserInfo userInfo = ScheduleConfig.getUserInfoService().get(message.getUserId());
        if(userInfo == null) {
            logger.error("查询用户信息失败，id=" + message.getUserId());
            R.fail("查询用户信息失败");
        }
        Server server = ScheduleConfig.getServerService().get(userInfo.getServerId());
        if(server == null || StringUtil.isEmpty(server.getServerUrl())) {
            logger.error("找不到服务器信息或服务器 serverUrl 为空，id=" + userInfo.getServerId());
            R.fail("找不到服务器信息或服务器 serverUrl 为空");
        }
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("docEntry", detailDocEntry);
        List<WarehouseNoticeDetail> warehouseNoticeDetailList = OkHttpUtil.getWarehouseNoticeDetailByDocEntry(server.getServerUrl(), hashMap);
        if(warehouseNoticeDetailList == null) {
            logger.error("查询入库详情信息失败");
            R.fail("查询入库详情信息失败");
        }
        return R.data(warehouseNoticeDetailList);
    }

    /**
     * 审核结果推送消息查看详情
     */
    private static R handleGetApprovalResultMessageInfo(int messageId, String codeId) {
        Message message = ScheduleConfig.getMessageService().get(messageId);
        if(message == null) {
            return R.fail("查询消息信息失败");
        }
        Message messageArgs = new Message();
        messageArgs.setId(message.getId());
        messageArgs.setClickTime(new Date());
        int result = ScheduleConfig.getMessageService().updateClickTime(messageArgs);
        if(result == 1) {
            logger.info("更新消息点击时间成功");
        } else {
            logger.error("更新消息点击时间失败");
            return R.fail("更新消息点击时间失败");
        }
        UserInfo userInfo = ScheduleConfig.getUserInfoService().get(message.getUserId());
        if(userInfo == null) {
            logger.error("查询用户信息失败，id=" + message.getUserId());
            R.fail("查询用户信息失败");
        }
        Server server = ScheduleConfig.getServerService().get(userInfo.getServerId());
        if(server == null || StringUtil.isEmpty(server.getServerUrl())) {
            logger.error("找不到服务器信息或服务器 serverUrl 为空，id=" + userInfo.getServerId());
            R.fail("找不到服务器信息或服务器 serverUrl 为空");
        }
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("codeId", codeId);
        List<ApprovalResultDetail> approvalResultDetailList = OkHttpUtil.getApprovalResultDetailByCodeID(server.getServerUrl(), hashMap);
        if(approvalResultDetailList == null) {
            logger.error("查询审批结果详情信息失败");
            return R.fail("查询审批结果详情信息失败");
        }
        return R.data(approvalResultDetailList);
    }

    public static void addUnbindUser(Server server, Customer customer, String userCode) {
        UnbindUser unbindUserSelect = new UnbindUser();
        unbindUserSelect.setServerId(server.getId());
        unbindUserSelect.setUserId(userCode);
        UnbindUser unbindUserGet = ScheduleConfig.getUserInfoService().selectUnbindUserByServerIdAndUserCode(unbindUserSelect);
        if(unbindUserGet == null) {
            UnbindUser unbindUserInsert = new UnbindUser();
            unbindUserInsert.setUserId(userCode);
            unbindUserInsert.setServerId(server.getId());
            unbindUserInsert.setServerIp(server.getServerIp());
            unbindUserInsert.setServerName(server.getServerName());
            unbindUserInsert.setCustomerName(customer.getCustomerName());
            int result = ScheduleConfig.getUserInfoService().addUnbindUser(unbindUserInsert);
            if(result == 0) {
                logger.error("添加未绑定用户失败，用户账号=" + unbindUserInsert.getUserId());
            }
        }
    }

    @GetMapping("/getMessageTemplateConfigList/{messageTemplateId}")
    public R getMessageTemplateConfigList(@PathVariable int messageTemplateId) {
        List<MessageTemplateConfig> messageTemplateConfigList = messageTemplateService.selectMessageTemplateConfigList(messageTemplateId);
        if(messageTemplateConfigList == null) {
            logger.error("查询消息模板配置信息失败，messageTemplateId = " + messageTemplateId);
            return R.fail("查询消息模板配置信息失败，messageTemplateId = " + messageTemplateId);
        }
        return R.data(messageTemplateConfigList);
    }
}
