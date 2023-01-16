package com.example.demo.entity;

import lombok.Data;

@Data
public class MessageType {

    public static final String ProcessToApproveMessageTime_Morning = "早上";

    public static final String ProcessToApproveMessageTime_Night = "晚上";

    public static final String ProcessToApproveMessageTime_MorningMessageName = "早消息";
//
    public static final String ProcessToApproveMessageTime_NightMessageName = "晚消息";

    private int id;

    private String messageName;

    private String scheduleTime;

    private int serverId;

    private int userId;

    private String description;

    private int status;

    private String messageTime;

    // 0为正常数据的值，1为删除数据的值
    private int deleted;

    // 非数据库字段
    private String userName;

    private String userCode;

    private String wxNickname;

    private String serverName;

    private String customerName;


    public enum enumMessageType {

        EMT_ProcessToApprove("流程待审批提醒", 1),
        EMT_ApprovalResult("审批结果通知", 2),
        EMT_ApprovalTimeout("审批超时通知", 3);

        private String name;
        private int index;

        private enumMessageType(String name, int index) {
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
