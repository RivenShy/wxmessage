package com.example.demo.entity;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
//@Entity
//@Table(name = "sys_permission")
public class SysPermission implements Serializable {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String code;
    private String name;
    private String url;
    private String type;
    private Integer pid;
//    private LocalDateTime createTime;
//    private LocalDateTime updateTime;
}