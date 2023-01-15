package com.example.demo.config;

import com.example.demo.entity.*;
import com.example.demo.service.*;
import org.apache.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

import static org.quartz.JobBuilder.newJob;

@Configuration
public class ScheduleConfig implements ApplicationContextAware {

    private static Logger logger = Logger.getLogger(ScheduleConfig.class);

    private static Scheduler scheduler;

    /**
     * 定时任务分组，目前都在group组
     */
    private static final String group = "group";

    private static MessageTypeService messageTypeService;

    private static UserInfoService userInfoService;

    private static MessageService messageService;

    private static ServerService serverService;

    private static CustomerService customerService;

    private static IConsultantService consultantService;

    private static IMessageTemplateService messageTemplateService;

    private static StringRedisTemplate stringRedisTemplate;

    private static  IApprovalConfigService approvalConfigService;

    private static  IMessageTemplateTriggerService messageTemplateTriggerService;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        messageTypeService = applicationContext.getBean(MessageTypeService.class);
        userInfoService = applicationContext.getBean(UserInfoService.class);
        messageService = applicationContext.getBean(MessageService.class);
        serverService = applicationContext.getBean(ServerService.class);
        customerService = applicationContext.getBean(CustomerService.class);
        consultantService = applicationContext.getBean(IConsultantService.class);
        messageTemplateService = applicationContext.getBean(IMessageTemplateService.class);
        stringRedisTemplate = applicationContext.getBean(StringRedisTemplate.class);
        approvalConfigService = applicationContext.getBean(IApprovalConfigService.class);
        messageTemplateTriggerService = applicationContext.getBean(IMessageTemplateTriggerService.class);
    }

    public static IMessageTemplateTriggerService getMessageTemplateTriggerService() {
        return messageTemplateTriggerService;
    }

    public static IApprovalConfigService getApprovalConfigService() {
        return approvalConfigService;
    }

    public static StringRedisTemplate getStringRedisTemplate() {
        return stringRedisTemplate;
    }

    public static IMessageTemplateService getMessageTemplateService() {
        return messageTemplateService;
    }

    public static ServerService getServerService() {
        return serverService;
    }

    public static MessageService getMessageService() {
        return messageService;
    }

    public static UserInfoService getUserInfoService() {
        return userInfoService;
    }

    public static CustomerService getCustomerService() {
        return customerService;
    }

    public static IConsultantService getConsultantService() {
        return consultantService;
    }

    public static void initSchedule() {
//        logger.info("initSchedule ++++++++++++++++++++++++++++++");
        List<MessageType> list = messageTypeService.list(0);
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            for(MessageType messageType : list) {
                if(messageType.getStatus() == 0 && messageType.getDeleted() == 0) {
                    String scheduleTime = messageType.getScheduleTime();
                    CronTrigger trigger = (CronTrigger) TriggerBuilder.newTrigger().withIdentity("" + messageType.getId(), group)
                            .withSchedule(CronScheduleBuilder.cronSchedule(scheduleTime)).build();
                    JobDetail job = newJob(ScheduleJob.class)
                            .withIdentity("" + messageType.getId(), group)
                            .usingJobData("id", messageType.getId())
                            .usingJobData("messageName", messageType.getMessageName())
                            .usingJobData("scheduleTime", messageType.getScheduleTime())
                            .usingJobData("serverId", messageType.getServerId())
                            .usingJobData("userId", messageType.getUserId())
                            .usingJobData("messageTime", messageType.getMessageTime())
                            .build();
                    scheduler.scheduleJob(job, trigger);
                }
            }
            scheduler.start();
//              for Test
//            Thread.sleep(10000);
//            MessageType messageType = new MessageType();
//            messageType.setId(1);
//            messageType.setMessageName("流程待审批提醒");
//            messageType.setScheduleTime("0 40 14 * * ?");
//            messageType.setServerId(1);
//            messageType.setUserId(1);
//            String scheduleTime = messageType.getScheduleTime();
//            scheduleTime = "0/5 * * * * ?";
//            String triggerkey = "trigger2" + messageType.getId();
//            CronTrigger trigger = (CronTrigger) TriggerBuilder.newTrigger().withIdentity(triggerkey, "group")
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
//            scheduler.start();
//            Thread.sleep(10 * 1000);
////            scheduler.deleteJob("job2" + messageType.getId());
////            scheduler.shutdown();
//            TriggerKey triggerKey = new TriggerKey(triggerkey, "group");
//            logger.info("triggerKey:" +  trigger);
//            CronTriggerImpl triggerRunning = (CronTriggerImpl) scheduler.getTrigger(triggerKey);
//            if (triggerRunning != null) {
//                logger.info("删掉trigger");
//                scheduler.unscheduleJob(triggerKey);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Customer getCustomerById(int customerId) {
        return customerService.get(customerId);
    }

    public static UserInfo getUserInfoById(int id) {
        UserInfo userInfo = userInfoService.get(id);
        return userInfo;
    }

    public static UserInfo getUserInfoByServerIdAndUserId(UserInfo userInfoArgs) {
        UserInfo userInfo = userInfoService.getUserInfoByServerIdAndUserId(userInfoArgs);
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

    public static Server getServerByServerId(int serverId) {
        return serverService.get(serverId);
    }


    public static boolean deleteScheduleJob(int id) {
        try {
            TriggerKey triggerKey = new TriggerKey(String.valueOf(id), group);
            CronTriggerImpl trigger = (CronTriggerImpl) scheduler.getTrigger(triggerKey);
            if (trigger != null) {
                logger.info("停用定时任务，定时消息id = " + id);
                scheduler.unscheduleJob(triggerKey);
                return true;
            } else {
                logger.error("找不到该定时任务，trigger key:" + id);
                return false;
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
            logger.error("删除定时任务失败");
            return false;
        }
    }


    public static boolean addSchduleJob(MessageType messageType) {
        try {
            String scheduleTime = messageType.getScheduleTime();
            CronTrigger trigger = (CronTrigger) TriggerBuilder.newTrigger().withIdentity("" + messageType.getId(), group)
                    .withSchedule(CronScheduleBuilder.cronSchedule(scheduleTime)).build();
            JobDetail job = newJob(ScheduleJob.class)
                    .withIdentity("" + messageType.getId(), group)
                    .usingJobData("id", messageType.getId())
                    .usingJobData("messageName", messageType.getMessageName())
                    .usingJobData("scheduleTime", messageType.getScheduleTime())
                    .usingJobData("serverId", messageType.getServerId())
                    .usingJobData("userId", messageType.getUserId())
                    .usingJobData("messageTime", messageType.getMessageTime())
                    .build();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
            logger.error("添加定时任务失败");
            return false;
        }
        logger.info("启用定时任务，定时消息id = " + messageType.getId());
        return true;
    }

    public static Consultant getConsultantByUserId(int consultantId) {
        return consultantService.get(consultantId);
    }
}
