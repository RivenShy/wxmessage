package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.result.R;
import com.example.demo.service.IMessageTemplateService;
import com.example.demo.service.IMessageTemplateTriggerService;
import com.example.demo.util.InvokeSetGetUtil;
import com.github.pagehelper.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/messageTemplateTrigger")
public class MessageTemplateTriggerController {
    private static Logger logger = Logger.getLogger(MessageTemplateTriggerController.class);

    @Autowired
    private IMessageTemplateTriggerService messageTemplateTriggerService;

    @Autowired
    private IMessageTemplateService messageTemplateService;

    @GetMapping("/getByMessageTemplateId")
    public R getByMessageTemplateId(@RequestParam("messageTemplateId") int messageTemplateId) {
        List<MessageTemplateTrigger> messageTemplateTriggerList = messageTemplateTriggerService.listByMessageTemplateId(messageTemplateId);
        if (messageTemplateTriggerList == null) {
            logger.error("查询消息发送触发条件表失败");
            return R.fail("查询消息发送触发条件表失败");
        }
        return R.data(messageTemplateTriggerList);
    }

    @PostMapping("/add")
    public R add(@RequestBody MessageTemplateTrigger messageTemplateTriggerArgs) {
        if (StringUtil.isEmpty(messageTemplateTriggerArgs.getTriggerFieldName()) || StringUtil.isEmpty(messageTemplateTriggerArgs.getConditionSymbol())
                || StringUtil.isEmpty(messageTemplateTriggerArgs.getThreshold())) {
            return R.fail("参数错误");
        }
        MessageTemplate messageTemplate = messageTemplateService.get(messageTemplateTriggerArgs.getMessageTemplateId());
        if (messageTemplate == null) {
            logger.error("没有该模板消息，请检查模板id,id = " + messageTemplateTriggerArgs.getMessageTemplateId());
            return R.fail("查询模板消息失败");
        }
        String triggerName = messageTemplateTriggerArgs.getTriggerFieldName();
        Object object = getTemplateTypeObject(messageTemplate);
        if (object == null) {
            logger.error("没有该模板消息，请检查模板id,id = " + messageTemplate.getWxTemplateId());
            return R.fail("没有该模板消息，请检查模板Id");
        }
        triggerName = triggerName.replaceAll("\\{", "");
        triggerName = triggerName.replaceAll("\\}", "");
        Class methodReturnType = InvokeSetGetUtil.getMethodReturnType(object, triggerName);
        if (methodReturnType == null) {
            logger.error("[" + messageTemplate.getTemplateName() + "]表单没有[" + messageTemplateTriggerArgs.getTriggerFieldNameDesc() + "]业务字段，请检查");
            return R.fail("[" + messageTemplate.getTemplateName() + "]表单没有[" + messageTemplateTriggerArgs.getTriggerFieldNameDesc() + "]业务字段，请检查");
        }
        if(!MessageTemplateTrigger.enumTriggerConditionSymbol.existEnumName(messageTemplateTriggerArgs.getConditionSymbol())) {
            logger.error("操作符输入有误，请检查，conditionSymbol = " + messageTemplateTriggerArgs.getConditionSymbol());
            return R.fail("操作符输入有误，请检查");
        }
        if (methodReturnType.equals(int.class)) {
            try {
                Integer.parseInt(messageTemplateTriggerArgs.getThreshold());
            } catch (NumberFormatException e) {
                return R.fail("输入格式有误,[" + messageTemplateTriggerArgs.getTriggerFieldNameDesc() + "]需要输入数字");
            }
        } else if (methodReturnType.equals(String.class)) {
            if (!messageTemplateTriggerArgs.getConditionSymbol().equals(MessageTemplateTrigger.enumTriggerConditionSymbol.EMTT_Equal.getName())) {
                return R.fail("[" + messageTemplateTriggerArgs.getTriggerFieldNameDesc() + "]只支持选择'等于'操作符");
            }
        } else if (methodReturnType.equals(Date.class)) {
            // 日期格式，目前需要统一
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                simpleDateFormat.parse(messageTemplateTriggerArgs.getThreshold());
            } catch (ParseException e) {
                return R.fail("输入格式有误，[" + messageTemplateTriggerArgs.getTriggerFieldNameDesc() + "]需要输入日期或选择日期输入");
            }
        }
        int result = messageTemplateTriggerService.add(messageTemplateTriggerArgs);
        return R.status(result == 1);
    }

    private Object getTemplateTypeObject(MessageTemplate messageTemplate) {
        Object object;
        if (messageTemplate.getId() == MessageTemplate.enumMessageTemplateType.EMTT_ProcessToApproveNoticeMorning.getIndex()) {
            object = new AuditDelayCount();
        } else if(messageTemplate.getId() == MessageTemplate.enumMessageTemplateType.EMTT_ProcessToApproveNoticeNight.getIndex()) {
            object = new PendingApproval();
        } else if(messageTemplate.getId() == MessageTemplate.enumMessageTemplateType.EMTT_WarehousingNotice.getIndex()) {
            object = new WarehouseNotice();
        } else if(messageTemplate.getId() == MessageTemplate.enumMessageTemplateType.EMTT_ProcessToApproveNoticeUrgent.getIndex()) {
            object = new PendingApprovalDetail();
        } else if(messageTemplate.getId() == MessageTemplate.enumMessageTemplateType.EMTT_ApprovalPass.getIndex()) {
            object = new ApprovalResult();
        } else if(messageTemplate.getId() == MessageTemplate.enumMessageTemplateType.EMTT_ApprovalReturn.getIndex()) {
            object = new ApprovalResult();
        } else if(messageTemplate.getId() == MessageTemplate.enumMessageTemplateType.EMTT_ApprovalTimeout.getIndex()) {
            object = new ApprovalTimeout();
        } else {
            return null;
        }
        return object;
    }

    @GetMapping("/delete")
    public R delete(@RequestParam("id") int id) {
        int result = messageTemplateTriggerService.deleteById(id);
        return R.status(result == 1);
    }

    @PostMapping("/customsql")
    public R customsql(@RequestBody MessageTemplateTrigger messageTemplateTrigger) {
        List<MessageTemplateTrigger> messageTemplateTriggerList = messageTemplateTriggerService.selectListByCustomsql(messageTemplateTrigger);
        if (messageTemplateTriggerList == null) {
            logger.error("查询消息发送触发条件表失败");
            return R.fail("查询消息发送触发条件表失败");
        }
        return R.data(messageTemplateTriggerList);
    }

    @GetMapping("/getMessageTemplateConfigAlertField/{messageTemplateId}")
    public R getMessageTemplateConfigAlertField(@PathVariable int messageTemplateId) {
        List<MessageTemplateConfig> messageTemplateConfigList = messageTemplateService.selectMessageTemplateConfigAlertField(messageTemplateId);
        if(messageTemplateConfigList == null) {
            logger.error("查询消息模板配置信息失败，messageTemplateId = " + messageTemplateId);
            return R.fail("查询消息模板配置信息失败，messageTemplateId = " + messageTemplateId);
        }
        return R.data(messageTemplateConfigList);
    }
}
