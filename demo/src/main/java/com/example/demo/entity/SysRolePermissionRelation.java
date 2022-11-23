package com.example.demo.entity;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
@Data
//@Entity
//@Table(name = "sys_role_permission")
public class SysRolePermissionRelation implements Serializable {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private Integer roleId;
    private Integer permissionId;
//    private LocalDateTime createTime;
//    private LocalDateTime updateTime;
}