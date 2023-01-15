package com.example.demo.entity;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
@Data
//@Entity
//@Table(name = "sys_role")
public class SysRole implements Serializable {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String roleName;
    private String roleCode;
    private String roleDesc;
//    private LocalDateTime createTime;
//    private LocalDateTime updateTime;
}