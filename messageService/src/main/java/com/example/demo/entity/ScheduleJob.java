package com.example.demo.entity;

import com.example.demo.config.ScheduleConfig;
import com.example.demo.controller.MessageTemplateController;
import com.example.demo.service.ServerService;
import com.example.demo.util.OkHttpUtil;
import com.github.pagehelper.StringUtil;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ScheduleJob implements Job {

    @Autowired
    private ServerService serverService;

    private static Logger logger = Logger.getLogger(ScheduleJob.class);

//    @Override
//    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
//        logger.info("quartz execute ++++++++++++++++++++++++++++++");
//    }

//    @Override
//    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
//        JobDetail detail = jobExecutionContext.getJobDetail();
//        int id = detail.getJobDataMap().getInt("id");
//        String messageName = detail.getJobDataMap().getString("messageName");
//        String scheduleTime = detail.getJobDataMap().getString("scheduleTime");
//        int serverId = detail.getJobDataMap().getInt("serverId");
//        int userId = detail.getJobDataMap().getInt("userId");
//        String messageTime = detail.getJobDataMap().getString("messageTime");
//        if(userId != -1) {
//            //
//            if(true) {
//                logger.info("给单个用户发送审批数据的sql暂时没有");
//                return;
//            }
//        } else {
//            // 给服务器Id为serverId的所有用户推送消息(如果有待审批和延迟审批的数据)
//            boolean sendSuccess = false;
//            if (messageName.equals(MessageType.enumMessageType.EMT_ProcessToApprove.getName())) {
//                Server server = ScheduleConfig.getServerByServerId(serverId);
//                if (server != null && server.getServerUrl() != null && !server.equals("")) {
//                    if("早上".equals(messageTime)) {
//                        // 早上统计消息
//                        List<AuditDelayCount> auditDelayCountList = OkHttpUtil.getAuditDelayCountList(server.getServerUrl());
//                        if (auditDelayCountList != null) {
//                            for (AuditDelayCount auditDelayCount : auditDelayCountList) {
//                                String userCode = auditDelayCount.getJobuser();
//                                UserInfo userInfoArgs = new UserInfo();
//                                userInfoArgs.setUserId(userCode);
//                                userInfoArgs.setServerId(serverId);
//                                UserInfo userInfo = ScheduleConfig.getUserInfoByServerIdAndUserId(userInfoArgs);
//                                if (userInfo == null) {
//                                    logger.error("查询用户信息失败，" + userInfoArgs);
//                                    continue;
//                                }
//                                Customer customer = ScheduleConfig.getCustomerById(server.getCustomerId());
//                                if (customer == null) {
//                                    logger.error("查询客户信息失败，id = " + server.getCustomerId());
//                                    continue;
//                                }
//                                if (userInfo.getOpenId() != null) {
//                                    // 发送消息前，先插入message表，这样才能得到消息Id，放到推送消息链接参数里
//                                    Message message = new Message();
//                                    message.setSendTime(new Date());
//                                    message.setUserId(userInfo.getId());
//                                    message.setMsgTypeId(id);
//                                    message.setMessageName("早消息");
//                                    // 默认失败，成功后再更新为0
//                                    message.setStatus(1);
//                                    int messageId = ScheduleConfig.addMessage(message);
//                                    if (messageId == 0) {
//                                        logger.error("添加消息失败");
//                                        continue;
//                                    }
//                                    Consultant consultant = null;
//                                    if(userInfo.getConsultantId() != 0) {
//                                        consultant = ScheduleConfig.getConsultantByUserId(userInfo.getConsultantId());
//                                    }
//                                    sendSuccess = WxUtil.sendProcessApprovalMsgToUser(customer.getCustomerName(), userInfo, messageId, auditDelayCount, consultant);
//                                    if (!sendSuccess) {
//                                        logger.error("发送" + messageName + "消息失败");
//                                    } else {
//                                        logger.info("发送" + messageName + "消息成功");
//                                        message.setId(messageId);
//                                        message.setStatus(0);
//                                        int result = ScheduleConfig.updateMessageStatus(message);
//                                        if (result == 1) {
//                                            logger.info("更新消息状态成功");
//                                        } else {
//                                            logger.error("更新消息状态失败");
//                                        }
//                                    }
//                                } else {
//                                    logger.error("编码为" + userInfo.getUserId() + "的用户的openId为null,无法推送微信消息");
//                                }
//                            }
//                        } else {
//                            logger.error("auditDelayCountList 为 null");
//                        }
//                    } else if("晚上".equals(messageTime)) {
//                        // 晚上统计消息
//                        List<PendingApproval> pendingApprovalList = OkHttpUtil.getPendingApprovalList(server.getServerUrl());
//                        if (pendingApprovalList != null) {
//                            for (PendingApproval pendingApproval : pendingApprovalList) {
//                                // 审核、未审都为0，跳过
//                                if(pendingApproval.getTodayCount() == 0 && pendingApproval.getAdcount() == 0) {
//                                    continue;
//                                }
//                                String userCode = pendingApproval.getJobuser();
//                                UserInfo userInfoArgs = new UserInfo();
//                                userInfoArgs.setUserId(userCode);
//                                userInfoArgs.setServerId(serverId);
//                                UserInfo userInfo = ScheduleConfig.getUserInfoByServerIdAndUserId(userInfoArgs);
//                                if (userInfo == null) {
//                                    logger.error("查询用户信息失败，" + userInfoArgs);
//                                    continue;
//                                }
//                                Customer customer = ScheduleConfig.getCustomerById(server.getCustomerId());
//                                if (customer == null) {
//                                    logger.error("查询客户信息失败，id = " + server.getCustomerId());
//                                    continue;
//                                }
//                                if (userInfo.getOpenId() != null) {
//                                    // 发送消息前，先插入message表，这样才能得到消息Id，放到推送消息链接参数里
//                                    Message message = new Message();
//                                    message.setSendTime(new Date());
//                                    message.setUserId(userInfo.getId());
//                                    message.setMsgTypeId(id);
//                                    message.setMessageName("晚消息");
//                                    // 默认失败，成功后再更新为0
//                                    message.setStatus(1);
//                                    int messageId = ScheduleConfig.addMessage(message);
//                                    if (messageId == 0) {
//                                        logger.error("添加消息失败");
//                                        continue;
//                                    }
//                                    Consultant consultant = null;
//                                    if(userInfo.getConsultantId() != 0) {
//                                        consultant = ScheduleConfig.getConsultantByUserId(userInfo.getConsultantId());
//                                    }
//                                    sendSuccess = WxUtil.sendProcessApprovalMsgToUserAtNight(customer.getCustomerName(), userInfo, messageId, pendingApproval, consultant);
//                                    if (!sendSuccess) {
//                                        logger.error("发送" + messageName + "消息失败");
//                                    } else {
//                                        logger.info("发送" + messageName + "消息成功");
//                                        message.setId(messageId);
//                                        message.setStatus(0);
//                                        int result = ScheduleConfig.updateMessageStatus(message);
//                                        if (result == 1) {
//                                            logger.info("更新消息状态成功");
//                                        } else {
//                                            logger.error("更新消息状态失败");
//                                        }
//                                    }
//                                } else {
//                                    logger.error("编码为" + userInfo.getUserId() + "的用户的openId为null,无法推送微信消息");
//                                }
//                            }
//                        } else {
//                            logger.error("auditDelayCountList 为 null");
//                        }
//                    } else {
//                        logger.error("未处理的时间，messageTime:" + messageTime);
//                    }
//                } else {
//                    logger.error("服务器或服务器url为null");
//                }
//            } else {
//                logger.error("未找到该模板消息，未处理的定时消息，messageName = " + messageName);
//            }
//        }
//    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDetail detail = jobExecutionContext.getJobDetail();
        int id = detail.getJobDataMap().getInt("id");
        String messageName = detail.getJobDataMap().getString("messageName");
        String scheduleTime = detail.getJobDataMap().getString("scheduleTime");
        int serverId = detail.getJobDataMap().getInt("serverId");
        int userId = detail.getJobDataMap().getInt("userId");
        String messageTime = detail.getJobDataMap().getString("messageTime");
        if(userId != -1) {
            //
            if(true) {
                logger.info("给单个用户发送审批数据的sql暂时没有");
                return;
            }
        } else {
            Server server = ScheduleConfig.getServerByServerId(serverId);
            if (server == null || StringUtil.isEmpty(server.getServerUrl())) {
                logger.error("查询服务器信息失败，或serverUrl为空");
                return;
            }
            MessageTemplate messageTemplate = null;
            // 给服务器Id为serverId的所有用户推送消息(如果有待审批和延迟审批的数据)
            if (messageName.equals(MessageType.enumMessageType.EMT_ProcessToApprove.getName())) {
                if (MessageType.ProcessToApproveMessageTime_Morning.equals(messageTime)) {
                    messageTemplate = ScheduleConfig.getMessageTemplateService().get(MessageTemplate.enumMessageTemplateType.EMTT_ProcessToApproveNoticeMorning.getIndex());
                } else if (MessageType.ProcessToApproveMessageTime_Night.equals(messageTime)) {
                    // 晚上统计消息
                    messageTemplate = ScheduleConfig.getMessageTemplateService().get(MessageTemplate.enumMessageTemplateType.EMTT_ProcessToApproveNoticeNight.getIndex());
                }
                messageTemplate.setServerIp(server.getServerIp());
                MessageTemplateController.handleCommonSendMessage(messageTemplate);
            } else if(MessageType.enumMessageType.EMT_ApprovalResult.getName().equals(messageName)) {
                // 发送审批结果提醒
                messageTemplate = ScheduleConfig.getMessageTemplateService().get(MessageTemplate.enumMessageTemplateType.EMTT_ApprovalPass.getIndex());
                messageTemplate.setServerIp(server.getServerIp());
                MessageTemplateController.handleCommonSendMessage(messageTemplate);
            } else if(MessageType.enumMessageType.EMT_ApprovalTimeout.getName().equals(messageName)) {
                // 发送审批超时提醒
                messageTemplate = ScheduleConfig.getMessageTemplateService().get(MessageTemplate.enumMessageTemplateType.EMTT_ApprovalTimeout.getIndex());
                messageTemplate.setServerIp(server.getServerIp());
                MessageTemplateController.handleCommonSendMessage(messageTemplate);
            }
            else {
                logger.error("未知的定时消息类型");
            }
        }
    }
}
