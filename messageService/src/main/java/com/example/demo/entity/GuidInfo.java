package com.example.demo.entity;

import lombok.Data;

@Data
public class GuidInfo {

    private String guid;

    private String account;

    private String serverIp;

    // 已扫码、已授权、已登录
    private String status;

    public enum enumGuidInfoStatus {
        EGS_NotYetScan("未扫码", 0),
        EGS_Scaned("已扫码", 1),
        EGS_Outhed("已授权", 2),
        EGS_Logined("已登录", 3);

        private String name;
        private int index;

        private enumGuidInfoStatus(String name, int index) {
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
