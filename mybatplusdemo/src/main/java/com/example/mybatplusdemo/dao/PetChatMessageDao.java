package com.example.mybatplusdemo.dao;

import com.example.mybatplusdemo.entity.websocket.PetChatMessage;
import com.example.mybatplusdemo.ma.base.PetBaseService;
import com.example.mybatplusdemo.mapper.PetChatMessageMapper;

public interface PetChatMessageDao extends PetBaseService<PetChatMessage> {

    default PetChatMessageMapper getMapper() {
        return (PetChatMessageMapper) this.getBaseMapper();
    }
//
//    @Override
//    default LambdaUpdateChainWrapper<PetChatMessage> lambdaUpdate() {
//        SysUser user = SecurityUtils.getLoginUser().getUser();
//        if(user != null) {
//            return PetBaseService.super.lambdaUpdate()
//                    .set(DBEntity::getLastUpdatedBy, user.getId())
//                    .set(DBEntity::getLastUpdateLogin, user.getId())
//                    .set(DBEntity::getLastUpdateDate, DateUtils.getTime());
//        }
//        return PetBaseService.super.lambdaUpdate().set(DBEntity::getLastUpdateDate, DateUtils.getTime());
//    }
}
