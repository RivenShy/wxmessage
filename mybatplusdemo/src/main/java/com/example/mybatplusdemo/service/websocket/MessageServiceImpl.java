package com.example.mybatplusdemo.service.websocket;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mybatplusdemo.config.websocket.UploadFile;
import com.example.mybatplusdemo.config.websocket.WebSocketServer;
import com.example.mybatplusdemo.entity.websocket.ChatMessage;
import com.example.mybatplusdemo.entity.websocket.Time;
import com.example.mybatplusdemo.mapper.MessageMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MessageServiceImpl implements MessageService {

    @Resource
    private MessageMapper messageMapper;

//    @Resource
    private WebSocketServer webSocketServer;


    /**
     * 获取聊天好友信息
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
//    @Override
//    public Map<String, Object> getFriendMsgList(String userId, int pageNum, int pageSize) {
//        Map<String,Object> resData = new HashMap<>();
//        PageHelper.startPage(pageNum,pageSize);
//        PageInfo<ChatMessage> info = new PageInfo<>(messageMapper.getFriendMsgList(userId));
//        resData.put("pagesNum",info.getPages());
//        resData.put("totalNum",info.getTotal());
//        resData.put("size",info.getSize());
//        resData.put("data", info.getList());
//        return resData;
//    }
    @Override
    public IPage<ChatMessage> getFriendMsgList(String userId, int pageNum, int pageSize) {
//        Map<String,Object> resData = new HashMap<>();
//        PageHelper.startPage(pageNum,pageSize);
//        PageInfo<ChatMessage> info = new PageInfo<>(messageMapper.getFriendMsgList(userId));
//        resData.put("pagesNum",info.getPages());
//        resData.put("totalNum",info.getTotal());
//        resData.put("size",info.getSize());
//        resData.put("data", info.getList());
//        return resData;

        IPage<ChatMessage> page = new Page<>(pageNum, pageSize);
        // 执行分页查询
        List<ChatMessage> chatMessageList = messageMapper.getFriendMsgList(page, userId);
        return page.setRecords(chatMessageList);
    }

    /**
     * 获取聊天信息
     * @param sendUserId
     * @param acceptUserId
     * @param pageNum
     * @param pageSize
     * @return
     */
//    @Override
//    public Map<String, Object> getChatMessage(String sendUserId,String acceptUserId,int pageNum,int pageSize) {
//        Map<String,Object> resData = new HashMap<>();
//        PageHelper.startPage(pageNum,pageSize);
//        PageInfo<ChatMessage> info = new PageInfo<>(messageMapper.getChatMessage(sendUserId,acceptUserId));
//        resData.put("pagesNum",info.getPages());
//        resData.put("totalNum",info.getTotal());
//        resData.put("size",info.getSize());
//        resData.put("data", info.getList());
//        return resData;
//    }
    @Override
    public Map<String,Object> getChatMessage(String sendUserId,String acceptUserId,int pageNum,int pageSize) {
        Map<String,Object> resData = new HashMap<>();
//        PageHelper.startPage(pageNum,pageSize);
//        PageInfo<ChatMessage> info = new PageInfo<>(messageMapper.getChatMessage(sendUserId,acceptUserId));
//        resData.put("pagesNum",info.getPages());
//        resData.put("totalNum",info.getTotal());
//        resData.put("size",info.getSize());
//        resData.put("data", info.getList());
//        return resData;

        IPage<ChatMessage> page = new Page<>(pageNum, pageSize);
        // 执行分页查询
        List<ChatMessage> chatMessageList = messageMapper.getChatMessage(page, sendUserId,acceptUserId);
        page.setRecords(chatMessageList);
        resData.put("pagesNum",page.getPages());
        resData.put("totalNum",page.getTotal());
        resData.put("size",page.getSize());
        resData.put("data", page.getRecords());
        return resData;
    }

    /**
     * 发送消息
     * @param chatMessage
     * @return
     */
    @Override
    public Map<String,Object> sendMsg(ChatMessage chatMessage) {
        Map<String,Object> repData = new HashMap<>();
        if(messageMapper.sendMsg(chatMessage.getSendUserId(), chatMessage.getAcceptUserId(), chatMessage.getContent(), chatMessage.getType(), chatMessage.getSoundTIme(), Time.getTime("yyyy-MM-dd HH:mm:ss"))) {
            try {
                List<Map<String,Object>> newMsg = (List<Map<String, Object>>) this.getChatMessage(chatMessage.getSendUserId(), chatMessage.getAcceptUserId(),1,1).get("data");
                webSocketServer.send(chatMessage.getSendUserId(), chatMessage.getAcceptUserId(), newMsg.get(0));
                repData.put("status",true);
                repData.put("returnMsg",newMsg.get(0));
            } catch (IOException e) {
                log.info("发送失败！");
            }
        }
        return repData;
    }

    /**
     * 发送聊天文件
     * @param sendUserId
     * @param acceptUserId
     * @param type
     * @param file
     * @return
     */
    @Override
    public Map<String, Object> sendFileMsg(String sendUserId, String acceptUserId, String type, Integer time, MultipartFile file) {
        Map<String,Object> repData = new HashMap<>();
        Map<String,Object> res = UploadFile.doRemoteUpload(file,"/file/");
        if ((Boolean) res.get("status")) {
            if (messageMapper.sendMsg(sendUserId,acceptUserId,(String) res.get("fileUrl"),type,time,Time.getTime("yyyy-MM-dd HH:mm:ss"))) {
                List<Map<String,Object>> newMsg = (List<Map<String, Object>>) this.getChatMessage(sendUserId,acceptUserId,1,1).get("data");
                try {
                    webSocketServer.send(sendUserId, acceptUserId, newMsg.get(0));
                }catch (IOException e) {
                    log.info("发送失败！");
                }
                repData.put("status",true);
                repData.put("returnMsg",newMsg.get(0));
            }
        }else {
            repData.put("status",false);
        }
        return repData;
    }

    /**iu i
     * 已读消息
     * @param sendUserId
     * @param acceptUserId
     * @return
     */
    @Override
    public Boolean readedMsg(String sendUserId, String acceptUserId) {
        return messageMapper.readedMsg(sendUserId,acceptUserId);
    }

    /**
     * 所有未读数
     * @param userId
     * @return
     */
    @Override
    public int getAllNoReadMsgNum(String userId) {
        return messageMapper.getAllNoReadMsgNum(userId);
    }



}