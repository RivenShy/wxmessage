package com.example.mybatplusdemo.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mybatplusdemo.dao.PetChatAccepterDao;
import com.example.mybatplusdemo.dao.PetChatMessageDao;
import com.example.mybatplusdemo.dao.PetChatSenderDao;
import com.example.mybatplusdemo.dto.GetMyChatMsgDto;
import com.example.mybatplusdemo.dto.PetChatMessageDto;
import com.example.mybatplusdemo.dto.PetChatMessageListQueryDto;
import com.example.mybatplusdemo.entity.BeanUtil;
import com.example.mybatplusdemo.entity.websocket.PetChatAccepter;
import com.example.mybatplusdemo.entity.websocket.PetChatMessage;
import com.example.mybatplusdemo.entity.websocket.PetChatSender;
import com.example.mybatplusdemo.mapper.PetChatMessageMapper;
import com.example.mybatplusdemo.service.PetChatMessageService;
import com.example.mybatplusdemo.utils.Condition;
import com.example.mybatplusdemo.utils.Query;
import com.example.mybatplusdemo.vo.PetChatMessageVo;
import com.google.protobuf.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

@Service
public class PetChatMessageServiceImpl extends ServiceImpl<PetChatMessageMapper, PetChatMessage> implements PetChatMessageService {

    @Resource
    private PetChatMessageDao chatMessageDao;

    @Resource
    private PetChatSenderDao chatSenderDao;

    @Resource
    private PetChatAccepterDao chatAccepterDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PetChatMessageVo sendMsg(PetChatMessageDto dto) {
        Long acceptUserId = dto.getAcceptUserId();
//        Assert.notNull(acceptUserId, MessageUtils.message("common.missing.request.param", "acceptUserId"));
//        Long sendUserId = SecurityUtils.getUserId();
        Long sendUserId = dto.getSendUserId();
        dto.setSendUserId(sendUserId);
        dto.setReaded(0);
        dto.setCreationDate(dto.getCreationDate());
        dto.setLastUpdateDate(dto.getCreationDate());
        if(!chatMessageDao.save(dto)) {
//            throw new ServiceException("send msg fail");
        }
        // 发送者
        PetChatSender chatSender = new PetChatSender();
        chatSender.setSendUserId(sendUserId);
        chatSender.setChatMessageId(dto.getId());
        chatSender.setCreationDate(dto.getCreationDate());
        chatSender.setLastUpdateDate(dto.getCreationDate());
        chatSenderDao.save(chatSender);
        // 接收者
        PetChatAccepter chatAccepter = new PetChatAccepter();
        chatAccepter.setAcceptUserId(acceptUserId);
        chatAccepter.setChatMessageId(dto.getId());
        chatAccepter.setCreationDate(dto.getCreationDate());
        chatAccepter.setLastUpdateDate(dto.getCreationDate());
        chatAccepterDao.save(chatAccepter);
        PetChatMessage chatMessage =  chatMessageDao.getById(dto.getId());
//        try {
//            webSocketServer.sendChatMsg(chatMessage);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        return BeanUtil.copyProperties(chatMessage, PetChatMessageVo.class);
    }

    @Override
    public Map<String, Object> getChatMessage(Long sendUserId, Long acceptUserId, int pageNum, int pageSize) {
        return Collections.emptyMap();
    }

    @Override
    public IPage<PetChatMessageVo> getPage(PetChatMessageListQueryDto dto, Query query) {
        IPage<PetChatMessageVo> page = chatMessageDao.getMapper().getPage(Condition.getPage(query), dto);
        return page;
    }

    @Override
    public IPage<PetChatMessageVo> getMyChatPage(GetMyChatMsgDto dto, Query query) {
//        Long userId = dto.getUserId();
//        Assert.notNull(userId, MessageUtils.message("common.missing.request.param", "userId"));
//        dto.setAcceptUserId(userId);
//        dto.setSendUserId(SecurityUtils.getUserId());
//        dto.setCurrentUserId(currentUserId);
        IPage<PetChatMessageVo> page = chatMessageDao.getMapper().getMyChatPage(Condition.getPage(query), dto);
        return page;
    }

    @Override
    public PetChatMessageVo sendFileMsg(Long sendUserId, Long acceptUserId, Integer type, Integer soundTime, MultipartFile file) {
        return null;
    }

    @Override
    public Integer getAllNoReadMsgNum() {
        return 0;
    }

    @Override
    public void delMsg(Long msgId) {

    }

    @Override
    public void delTargetUserMsg(Long userId) {

    }
}
