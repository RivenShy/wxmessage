package com.example.mybatplusdemo.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.mybatplusdemo.dto.GetMyChatMsgDto;
import com.example.mybatplusdemo.dto.PetChatMessageDto;
import com.example.mybatplusdemo.dto.PetChatMessageListQueryDto;
import com.example.mybatplusdemo.service.PetChatMessageService;
import com.example.mybatplusdemo.utils.Query;
import com.example.mybatplusdemo.utils.R;
import com.example.mybatplusdemo.vo.PetChatMessageVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@Api(value = "聊天消息管理", tags = "聊天消息管理")
@RequestMapping("/chatMessage")
public class PetChatMessageController {

    @Resource
    private PetChatMessageService messageService;

//    @RateLimiter(time = 86400, count = 1000, limitType = LimitType.IP)
    @PostMapping("sendMsg")
    @ApiOperation(value = "当前用户发送消息给某人")
    public R<PetChatMessageVo> sendMsg(@RequestBody PetChatMessageDto dto) {
        return R.data(messageService.sendMsg(dto));
    }

    @GetMapping("/getPage")
    @ApiOperation(value = "聊天信息列表分页")
    public R<IPage<PetChatMessageVo>> getPage(PetChatMessageListQueryDto dto, Query query) {
        return R.data(messageService.getPage(dto, query));
    }

    @GetMapping("/getMyChatPage")
    @ApiOperation(value = "我与某用户的聊天信息列表分页")
    public R<IPage<PetChatMessageVo>> getMyChatPage(GetMyChatMsgDto dto, Query query) {
        return R.data(messageService.getMyChatPage(dto, query));
    }

}
