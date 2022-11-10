package com.example.demo.entity;

import com.example.demo.config.ScheduleConfig;
import com.example.demo.util.WxUtil;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;

public class MyJob implements Job {

    private static Logger logger = Logger.getLogger(MyJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDetail detail = jobExecutionContext.getJobDetail();
        int id = detail.getJobDataMap().getInt("id");
        String messageName = detail.getJobDataMap().getString("messageName");
        String scheduleTime = detail.getJobDataMap().getString("scheduleTime");
        int serverId = detail.getJobDataMap().getInt("serverId");
        int userId = detail.getJobDataMap().getInt("userId");
        System.out.println("MyJob is working");
        System.out.println("id = " + id);
        System.out.println("messageName = " + messageName);
        System.out.println("scheduleTime = " + scheduleTime);
        System.out.println("serverId = " + serverId);
        System.out.println("userId = " + userId);

        UserInfo userInfo = ScheduleConfig.getUserInfoById(userId);
        if(userInfo != null && userInfo.getOpenId() != null) {
            if(messageName.equals("流程待审批提醒")) {
                boolean sendSuccess = WxUtil.sendProcessApprovalMsgToUser(userInfo.getOpenId(), id);
                // 插入message表
                Message message = new Message();
                message.setSendTime(new Date());
                message.setUserId(userId);
                message.setMsgTypeId(id);
                message.setStatus(0);
                if(!sendSuccess) {
                    message.setStatus(1);
                } else{
                    logger.error("发送流程待审批提醒消息失败");
                }
                ScheduleConfig.addMessage(message);
            } else {
                logger.info("messageName = " + messageName);
            }
        } else {
            logger.error("根据id查询用户信息失败：" + userInfo);
        }
    }
}
