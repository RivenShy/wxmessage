package com.example.mybatplusdemo.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.mybatplusdemo.dto.GetMyChatMsgDto;
import com.example.mybatplusdemo.dto.PetChatMessageDto;
import com.example.mybatplusdemo.dto.PetChatMessageListQueryDto;
import com.example.mybatplusdemo.utils.Query;
import com.example.mybatplusdemo.vo.PetChatMessageVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface PetChatMessageService {

    PetChatMessageVo sendMsg(PetChatMessageDto dto);

    Map<String,Object> getChatMessage(Long sendUserId, Long acceptUserId, int pageNum, int pageSize);

    /**
     * 聊天信息列表分页
     * @param dto
     * @param query
     * @return
     */
    IPage<PetChatMessageVo> getPage(PetChatMessageListQueryDto dto, Query query);

    /**
     * 我与某用户的聊天信息列表分页
     * @param dto
     * @param query
     * @return
     */
    IPage<PetChatMessageVo> getMyChatPage(GetMyChatMsgDto dto, Query query);

    /**
     * 获取我的聊天列表
     * @param dto
     * @param query
     * @return
     */
//    IPage<PetChatMessageVo> getFriendMsgList(GetFriendMsgDto dto, Query query);

    /**
     * 发送聊天文件
     * @return
     */
    PetChatMessageVo sendFileMsg(Long sendUserId, Long acceptUserId, Integer type, Integer soundTime, MultipartFile file);

    /**
     * 已读消息
     * @param dto
     */
//    void readedMsg(ReadedMsgDto dto);

    /**
     * getAllNoReadMsgNum
     * @return
     */
    Integer getAllNoReadMsgNum();

    /**
     * 删除单条消息
     * @param msgId
     */
    void delMsg(Long msgId);

    /**
     * 删除当前用户与目标用户的聊天消息
     * @param userId
     */
    void delTargetUserMsg(Long userId);
}
