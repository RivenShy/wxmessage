package com.example.mybatplusdemo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class DBEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public DBEntity() {

    }

    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    protected Long id;

    @ApiModelProperty("创建者id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createdBy;

    @ApiModelProperty("创建日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date creationDate;

    @ApiModelProperty("最后更新日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastUpdateDate;

    @ApiModelProperty("最后更新人")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long lastUpdatedBy;

    @ApiModelProperty("最后登录人")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long lastUpdateLogin;

    @ApiModelProperty("创建部门")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createDept;

    @ApiModelProperty("状态")
    protected Integer status;
}
