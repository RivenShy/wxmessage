package com.example.mybatplusdemo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PetBaseEntity extends DBEntity {

    @TableLogic
    @ApiModelProperty("是否已删除【0  否、1  是】")
    private Integer isDelete;
}
