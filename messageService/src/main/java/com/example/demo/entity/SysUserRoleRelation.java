package com.example.demo.entity;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
@Data
//@Entity
//@Table(name = "sys_user_role")
public class SysUserRoleRelation implements Serializable {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer roleId;
//    private LocalDateTime createTime;
//    private LocalDateTime updateTime;
}