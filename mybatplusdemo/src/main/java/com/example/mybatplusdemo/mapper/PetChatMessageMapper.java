package com.example.mybatplusdemo.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.example.mybatplusdemo.dto.GetMyChatMsgDto;
import com.example.mybatplusdemo.dto.PetChatMessageListQueryDto;
import com.example.mybatplusdemo.entity.websocket.PetChatMessage;
import com.example.mybatplusdemo.vo.PetChatMessageVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PetChatMessageMapper extends BaseMapper<PetChatMessage> {
    IPage<PetChatMessageVo> getPage(IPage<PetChatMessageVo> page, @Param(Constants.WRAPPER) PetChatMessageListQueryDto dto);

    IPage<PetChatMessageVo> getMyChatPage(IPage<PetChatMessageVo> page, @Param(Constants.WRAPPER) GetMyChatMsgDto dto);
}
