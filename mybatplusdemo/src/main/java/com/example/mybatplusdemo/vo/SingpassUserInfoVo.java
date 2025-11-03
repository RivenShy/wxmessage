package com.example.mybatplusdemo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SingpassUserInfoVo {

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("NRIC/FIN用户身份编号")
    private String uinfin;

    @ApiModelProperty("国籍(只有两个字母)")
    private String nationality;

    @ApiModelProperty("生日")
    private String dob;

    @ApiModelProperty("sub(唯一标识uuid)")
    private String sub;
}
