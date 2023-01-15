package com.example.mybatplusdemo.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("user")
public class User {
    //设置ID生成策略为AUTO
//    @TableId(type= IdType.AUTO)
//    @TableId(type= IdType.INPUT)
//    @TableId(type= IdType.ASSIGN_ID)
//    @TableId(type = IdType.ASSIGN_UUID)
    @TableId
    private Long id;
//    @TableField(value="name", select=false)
    @TableField("name")
    private String nameXXX;
    private Integer age;
    private String email;
    @TableField(exist=false)
    private String online;

    @TableField(value="deleted")
    //value为正常数据的值，delval为删除数据的值
    @TableLogic(value="0",delval = "1")
    private Integer deleted;

    @Version
    private Integer version;
}