package com.example.demo.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class MessageTemplate implements Serializable {

    private int id;

    // 模板名称
    private String templateName;

    private String wxTemplateId;

    private int deleted;

    private String detailUrl;

    private String firstData;

    private String firstDataColor;

    private String keyword1Data;

    private String keyword1DataColor;

    private String keyword2Data;

    private String keyword2DataColor;

    private String keyword3Data;

    private String keyword3DataColor;

    private String keyword4Data;

    private String keyword4DataColor;

    private String keyword5Data;

    private String keyword5DataColor;

    private String remarkData;

    private String remarkDataColor;

    private String description;

    // 传参，待审批
    private String serverIp;
    private String messageTime;

    // 主表唯一键（如入库表Id）
    private String masterTableUniqueKey;

    // 从表唯一键（如入库详情表Id）
    private String slaveTableUniqueKey;

    // 用户账号
    private String userCode;

    public enum enumMessageTemplateType {

        EMTT_ProcessToApproveNoticeMorning("流程待审批提醒(早消息)", 1),
        EMTT_ProcessToApproveNoticeNight("流程待审批提醒（晚消息）", 2),
        EMTT_WarehousingNotice("入库通知", 3),
        EMTT_ProcessToApproveNoticeUrgent("流程待审批提醒(指定推送)", 4),
        EMTT_ApprovalPass("审批通过通知", 5),
        EMTT_ApprovalReturn("流程审批退回通知", 6),
        EMTT_ApprovalTimeout("审批超时通知", 7);

        private String name;
        private int index;

        private enumMessageTemplateType(String name, int index) {
            this.name = name;
            this.index = index;
        }
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }
}
