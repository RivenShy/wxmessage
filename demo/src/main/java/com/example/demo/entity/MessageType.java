package com.example.demo.entity;

import lombok.Data;

@Data
public class MessageType {

    private int id;

    private String messageName;

    private String scheduleTime;

    private int serverId;

    private int userId;

    private String description;

    private int status;

    private String messageTime;

    // 非数据库字段
    private String userName;

    private String userCode;

    private String wxNickname;

    private String serverName;

    private String customerName;


    public enum enumMessageType {

        EMT_ProcessToApprove("流程待审批提醒", 1),
        EMT_RemoteLogin("异地登录提醒", 2);

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
