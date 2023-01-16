package com.example.demo.entity;

import lombok.Data;
import java.util.Date;

@Data
public class ApprovalDetail {

    private int id;

    private Date approvedTime;

    private Date lastUserApprovedTime;

    private String jobuser;
}
