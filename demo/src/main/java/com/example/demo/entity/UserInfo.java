package com.example.demo.entity;

import lombok.Data;

import javax.persistence.*;

//@Entity
//@Table(name = "user")
@Data
public class User {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id")
    private int id;

//    @Column(name = "name")
    private String name;

//    @Column(name = "openid")
    private String openId;

//    @Column(name = "wxnickname")
    private String wxNickname;
}

