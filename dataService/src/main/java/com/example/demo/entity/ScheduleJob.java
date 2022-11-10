package com.example.demo.entity;

import com.example.demo.config.ScheduleConfig;
import com.example.demo.util.WxUtil;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;

public class ScheduleJob implements Job {

    private static Logger logger = Logger.getLogger(ScheduleJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDetail detail = jobExecutionContext.getJobDetail();
        int id = detail.getJobDataMap().getInt("id");
        String messageName = detail.getJobDataMap().getString("messageName");
        String scheduleTime = detail.getJobDataMap().getString("scheduleTime");
        int serverId = detail.getJobDataMap().getInt("serverId");
        int userId = detail.getJobDataMap().getInt("userId");
//        System.out.println("MyJob is working");
//        System.out.println("id = " + id);
//        System.out.println("messageName = " + messageName);
//        System.out.println("scheduleTime = " + scheduleTime);
//        System.out.println("serverId = " + serverId);
//        System.out.println("userId = " + userId);

        UserInfo userInfo = ScheduleConfig.getUserInfoById(userId);
        if(userInfo != null && userInfo.getOpenId() != null) {
            // 发送消息前，先插入message表，这样才能得到消息Id，放到推送消息链接参数里
            Message message = new Message();
            message.setSendTime(new Date());
            message.setUserId(userId);
            message.setMsgTypeId(id);
            // 默认失败，成功后再更新为0
            message.setStatus(1);
            int messageId = ScheduleConfig.addMessage(message);
            if(messageId == 0) {
                logger.error("添加消息失败");
                return;
            }
            logger.info("添加消息成功");
            if(messageName.equals(MessageType.enumMessageType.EMT_ProcessToApprove.getName())) {
                boolean sendSuccess = WxUtil.sendProcessApprovalMsgToUser(userInfo.getOpenId(), messageId);
                if(!sendSuccess) {
                    message.setStatus(1);
                    logger.error("发送流程待审批提醒消息失败");
                } else{
                    logger.info("发送流程待审批提醒消息成功");
                    message.setId(messageId);
                    message.setStatus(0);
                    int result = ScheduleConfig.updateMessageStatus(message);
                    if(result == 1) {
                        logger.info("更新消息状态成功");
                    } else {
                        logger.error("更新消息状态失败");
                    }
                }
            } else {
                logger.info("未处理的定时消息，messageName = " + messageName);
            }
        } else {
            logger.error("根据id查询用户信息失败：" + userInfo);
        }
    }
}
