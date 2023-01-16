package com.example.demo.entity;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
//@Data
//@Entity
//@Table(name = "sys_user")
@Data
public class SysUser implements Serializable {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String username;
    private String password;
    private boolean enabled;
    private Integer orgId;
//    private LocalDateTime createTime;
//    private LocalDateTime updateTime;
}