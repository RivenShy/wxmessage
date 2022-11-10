package com.example.demo.config;

import com.example.demo.entity.Message;
import com.example.demo.entity.MessageType;
import com.example.demo.entity.ScheduleJob;
import com.example.demo.entity.UserInfo;
import com.example.demo.service.MessageService;
import com.example.demo.service.MessageTypeService;
import com.example.demo.service.UserInfoService;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static org.quartz.JobBuilder.newJob;

@Configuration
public class ScheduleConfig implements ApplicationContextAware {

    private static MessageTypeService messageTypeService;

    private static UserInfoService userInfoService;

    private static MessageService messageService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        messageTypeService = applicationContext.getBean(MessageTypeService.class);
        userInfoService = applicationContext.getBean(UserInfoService.class);
        messageService = applicationContext.getBean(MessageService.class);
    }

    public static void initSchedule() {
        List<MessageType> list = messageTypeService.list();
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            for(MessageType messageType : list) {
                String scheduleTime = messageType.getScheduleTime();
//                scheduleTime = "0/1 * * * * ?";
                CronTrigger trigger = (CronTrigger) TriggerBuilder.newTrigger().withIdentity("trigger" + messageType.getId(), "group")
                        .withSchedule(CronScheduleBuilder.cronSchedule(scheduleTime)).build();
                JobDetail job = newJob(ScheduleJob.class)
                        .withIdentity("job" + messageType.getId(), "group")
                        .usingJobData("id", messageType.getId())
                        .usingJobData("messageName", messageType.getMessageName())
                        .usingJobData("scheduleTime", messageType.getScheduleTime())
                        .usingJobData("serverId", messageType.getServerId())
                        .usingJobData("userId", messageType.getUserId())
                        .build();
                scheduler.scheduleJob(job, trigger);
            }
            scheduler.start();
//            Thread.sleep(10000);
//            MessageType messageType = new MessageType();
//            messageType.setId(1);
//            messageType.setMessageName("流程待审批提醒");
//            messageType.setScheduleTime("0 40 14 * * ?");
//            messageType.setServerId(1);
//            messageType.setUserId(1);
//            String scheduleTime = messageType.getScheduleTime();
////                scheduleTime = "0/1 * * * * ?";
//            CronTrigger trigger = (CronTrigger) TriggerBuilder.newTrigger().withIdentity("trigger2" + messageType.getId(), "group")
//                    .withSchedule(CronScheduleBuilder.cronSchedule(scheduleTime)).build();
//            JobDetail job = newJob(ScheduleJob.class)
//                    .withIdentity("job2" + messageType.getId(), "group")
//                    .usingJobData("id", messageType.getId())
//                    .usingJobData("messageName", messageType.getMessageName())
//                    .usingJobData("scheduleTime", messageType.getScheduleTime())
//                    .usingJobData("serverId", messageType.getServerId())
//                    .usingJobData("userId", messageType.getUserId())
//                    .build();
//            scheduler.scheduleJob(job, trigger);
//            scheduler.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static UserInfo getUserInfoById(int id) {
        UserInfo userInfo = userInfoService.get(id);
        return userInfo;
    }

    public static int addMessage(Message messageArgs) {
        int result = messageService.add(messageArgs);
        if(result == 1) {
            return messageArgs.getId();
        } else {
            return 0;
        }
    }

    public static int updateMessageStatus(Message messageArgs) {
        int result = messageService.updateStatus(messageArgs);
        return result;
    }
}
