package com.example.demo.entity;

import com.example.demo.config.ScheduleConfig;
import com.example.demo.service.ServerService;
import com.example.demo.util.OkHttpUtil;
import com.example.demo.util.WxUtil;
import com.github.pagehelper.StringUtil;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class ScheduleJob implements Job {

    @Autowired
    private ServerService serverService;

    private static Logger logger = Logger.getLogger(ScheduleJob.class);

//    @Override
//    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
//        logger.info("quartz execute ++++++++++++++++++++++++++++++");
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
//        System.out.println("MyJob is working");
//        System.out.println("id = " + id);
//        System.out.println("messageName = " + messageName);
//        System.out.println("scheduleTime = " + scheduleTime);
//        System.out.println("serverId = " + serverId);
//        System.out.println("userId = " + userId);
        if(userId != -1) {
            //
            if(true) {
                logger.info("给单个用户发送审批数据的sql暂时没有");
                return;
            }
//            UserInfo userInfo = ScheduleConfig.getUserInfoById(userId);
//            if (userInfo != null && userInfo.getOpenId() != null) {
//                // 发送消息前，先插入message表，这样才能得到消息Id，放到推送消息链接参数里
//                Message message = new Message();
//                message.setSendTime(new Date());
//                message.setUserId(userId);
//                message.setMsgTypeId(id);
//                // 默认失败，成功后再更新为0
//                message.setStatus(1);
//                int messageId = ScheduleConfig.addMessage(message);
//                if (messageId == 0) {
//                    logger.error("添加消息失败");
//                    return;
//                }
//                logger.info("添加消息成功");
//                boolean sendSuccess = false;
//                if (messageName.equals(MessageType.enumMessageType.EMT_ProcessToApprove.getName())) {
//                    Server server = ScheduleConfig.getServerByServerId(userInfo.getServerId());
//                    if (server != null && server.getServerUrl() != null && !server.equals("")) {
////                        List<PendingApproval> pendingApprovalList = OkHttpUtil.getPendingApprovalList(server.getServerUrl());
//                        List<AuditDelayCount> auditDelayCountList = OkHttpUtil.getAuditDelayCountList(server.getServerUrl());
//                        if (auditDelayCountList != null) {
//                            sendSuccess = WxUtil.sendProcessApprovalMsgToUser(userInfo.getOpenId(), messageId, auditDelayCountList.size());
//                        } else {
//                            logger.error("pendingApprovalList 为 null");
//                        }
//                    } else {
//                        logger.error("服务器或服务器url为null");
//                    }
//                } else if (messageName.equals(MessageType.enumMessageType.EMT_RemoteLogin.getName())) {
//                    sendSuccess = WxUtil.sendRemoteLoginMsg(userInfo.getOpenId(), message.getId());
//                } else {
//                    logger.info("未处理的定时消息，messageName = " + messageName);
//                }
//                if (!sendSuccess) {
//                    message.setStatus(1);
//                    logger.error("发送" + messageName + "消息失败");
//                } else {
//                    logger.info("发送" + messageName + "消息成功");
//                    message.setId(messageId);
//                    message.setStatus(0);
//                    int result = ScheduleConfig.updateMessageStatus(message);
//                    if (result == 1) {
//                        logger.info("更新消息状态成功");
//                    } else {
//                        logger.error("更新消息状态失败");
//                    }
//                }
//            } else {
//                logger.error("根据id查询用户信息失败,或用户openId为null：" + userInfo);
//            }
        } else {
            // 给服务器Id为serverId的所有用户推送消息(如果有待审批和延迟审批的数据)
            boolean sendSuccess = false;
            if (messageName.equals(MessageType.enumMessageType.EMT_ProcessToApprove.getName())) {
                Server server = ScheduleConfig.getServerByServerId(serverId);
                if (server != null && server.getServerUrl() != null && !server.equals("")) {
                    if(beforeTwelveClock() && (!StringUtil.isEmpty(messageTime) && messageName.equals("早上"))) {
                        // 早上统计消息
                        List<AuditDelayCount> auditDelayCountList = OkHttpUtil.getAuditDelayCountList(server.getServerUrl());
                        if (auditDelayCountList != null) {
                            for (AuditDelayCount auditDelayCount : auditDelayCountList) {
                                String userCode = auditDelayCount.getJobuser();
                                UserInfo userInfoArgs = new UserInfo();
                                userInfoArgs.setUserId(userCode);
                                userInfoArgs.setServerId(serverId);
                                UserInfo userInfo = ScheduleConfig.getUserInfoByServerIdAndUserId(userInfoArgs);
                                if (userInfo == null) {
                                    logger.error("查询用户信息失败，" + userInfoArgs);
                                    return;
                                }
                                Customer customer = ScheduleConfig.getCustomerById(server.getCustomerId());
                                if (customer == null) {
                                    logger.error("查询客户信息失败，id = " + server.getCustomerId());
                                    return;
                                }
                                if (userInfo.getOpenId() != null) {
                                    // 发送消息前，先插入message表，这样才能得到消息Id，放到推送消息链接参数里
                                    Message message = new Message();
                                    message.setSendTime(new Date());
                                    message.setUserId(userInfo.getId());
                                    message.setMsgTypeId(id);
                                    // 默认失败，成功后再更新为0
                                    message.setStatus(1);
                                    int messageId = ScheduleConfig.addMessage(message);
                                    if (messageId == 0) {
                                        logger.error("添加消息失败");
                                        return;
                                    }
                                    sendSuccess = WxUtil.sendProcessApprovalMsgToUser(customer.getCustomerName(), userInfo, messageId, auditDelayCount);
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
                        } else {
                            logger.error("auditDelayCountList 为 null");
                        }
                    } else if(!StringUtil.isEmpty(messageTime) && messageName.equals("晚上")) {
                        // 晚上统计消息
                        List<PendingApproval> pendingApprovalList = OkHttpUtil.getPendingApprovalList(server.getServerUrl());
                        if (pendingApprovalList != null) {
                            for (PendingApproval pendingApproval : pendingApprovalList) {
                                String userCode = pendingApproval.getJobuser();
                                UserInfo userInfoArgs = new UserInfo();
                                userInfoArgs.setUserId(userCode);
                                userInfoArgs.setServerId(serverId);
                                UserInfo userInfo = ScheduleConfig.getUserInfoByServerIdAndUserId(userInfoArgs);
                                if (userInfo == null) {
                                    logger.error("查询用户信息失败，" + userInfoArgs);
                                    continue;
                                }
                                Customer customer = ScheduleConfig.getCustomerById(server.getCustomerId());
                                if (customer == null) {
                                    logger.error("查询客户信息失败，id = " + server.getCustomerId());
                                    return;
                                }
                                if (userInfo.getOpenId() != null) {
                                    // 发送消息前，先插入message表，这样才能得到消息Id，放到推送消息链接参数里
                                    Message message = new Message();
                                    message.setSendTime(new Date());
                                    message.setUserId(userInfo.getId());
                                    message.setMsgTypeId(id);
                                    // 默认失败，成功后再更新为0
                                    message.setStatus(1);
                                    int messageId = ScheduleConfig.addMessage(message);
                                    if (messageId == 0) {
                                        logger.error("添加消息失败");
                                        return;
                                    }
                                    sendSuccess = WxUtil.sendProcessApprovalMsgToUserAtNight(customer.getCustomerName(), userInfo, messageId, pendingApproval);
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
                        } else {
                            logger.error("auditDelayCountList 为 null");
                        }
                    } else {
                        logger.error("未处理的时间，messageTime:" + messageTime);
                    }
                } else {
                    logger.error("服务器或服务器url为null");
                }
            } else {
                logger.error("未找到该模板消息，未处理的定时消息，messageName = " + messageName);
            }
        }
    }

    private boolean beforeTwelveClock() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();// 取当前日期。
        Calendar calendar = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),12,0,0);
        System.out.println(format.format(calendar.getTime()));
        Date date = new Date();
        return date.before(calendar.getTime());
    }
}
