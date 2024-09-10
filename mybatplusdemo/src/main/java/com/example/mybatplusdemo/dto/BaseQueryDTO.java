package com.example.mybatplusdemo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class BaseQueryDTO {

    @ApiModelProperty(value = "销售代表工号")
    private String scopeSalesCode;

    @ApiModelProperty(value = "销售小组id")
    private List<Long> scopeTeamAgencyIds;

    @ApiModelProperty(value = "库存组织ID")
    private List<Long> scopeOrganizationIds;
}
