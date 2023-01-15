package com.example.demo.entity;

import lombok.Data;

@Data
public class MessageTemplateTrigger {

    private int id;

    private int messageTemplateId;

    private String triggerFieldName;

    private String triggerFieldNameDesc;

    private String conditionSymbol;

    private String threshold;

    private String description;

    private int deleted;

    private String customsql;

    public enum enumTriggerConditionSymbol {

        EMTT_Equal("等于", 1),
        EMTT_Less("小于", 2),
        EMTT_Greater("大于", 3);

        private String name;
        private int index;

        private enumTriggerConditionSymbol(String name, int index) {
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

        public static boolean existEnumName(String name) {
            for (enumTriggerConditionSymbol c : enumTriggerConditionSymbol.values()) {
                if (c.getName().equals(name)) {
                    return true;
                }
            }
            return false;
        }
    }
}
