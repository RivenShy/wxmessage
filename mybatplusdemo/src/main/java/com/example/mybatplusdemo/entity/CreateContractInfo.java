package com.example.mybatplusdemo.entity;

import lombok.Data;

import java.util.List;

@Data
public class CreateContractInfo {

    private String contractName;

    private String contractNo;

    private String firstPartyName;

    private String partyBName;

    private String handledByName;

    private List<FreeContractClauseDto> contractClauseDtos;

    private List<FreeContractClauseDto> deviceContractClauseDtos;

    private List<FreeBillDetailVo> elseFreeBillDetailVos;

    private List<FreeBillDetailVo> freeBillDetailVos;
}
