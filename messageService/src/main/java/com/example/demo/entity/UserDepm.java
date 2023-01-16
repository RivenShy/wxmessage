package com.example.demo.entity;

import lombok.Data;

import java.util.Date;

@Data
public class UserDepm {

    private String DefaultDBName;

    private String UserName;

    private String KeyID;

    private String Pswd;

    private String MDPswd;

    private String Description;

    private char Sex;

    private String Operator;

    private Date Opdate;

    private Date MainDFM;

    private String sysType;

    private String RoleGroup;

    private Date opertime;

    private String remark;

    private int IsUse;

    private String DeptID;

    private String DeptName;

    private String jobTitle;

    private String U_GangWei;

//    private Char

    @Override
    public String toString() {
        return "UserDepm{" +
                "DefaultDBName='" + DefaultDBName + '\'' +
                ", UserName='" + UserName + '\'' +
                ", KeyID='" + KeyID + '\'' +
                ", Pswd='" + Pswd + '\'' +
                ", MDPswd='" + MDPswd + '\'' +
                ", Description='" + Description + '\'' +
                '}';
    }
}
