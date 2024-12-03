package com.example.mybatplusdemo.controller.websocket;


import com.example.mybatplusdemo.entity.websocket.ChatMessage;
import com.example.mybatplusdemo.mapper.MessageMapper;
import com.example.mybatplusdemo.service.websocket.MessageServiceImpl;
import com.example.mybatplusdemo.utils.R;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Map;

/**
 *   JsonResult 为自定义json序列化方法,用自己的方法即可
 *
 **/

@RestController
@RequestMapping("/msg")
public class MsgApi {

    @Resource
    private MessageServiceImpl messageService;

    @Resource
    private MessageMapper messageMapper;

    /**
     * 获取好友列表API
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */

    @PostMapping("/getFriendMsgList")
    public R getFriendMsgList(@RequestParam(value = "userId") String userId,
                              @RequestParam(value = "pageNum") int pageNum,
                              @RequestParam(value = "pageSize") int pageSize) {
        return R.data(messageService.getFriendMsgList(userId,pageNum,pageSize));
    }

    /**
     * 获取聊天信息API
     * @param sendUserId
     * @param acceptUserId
     * @param pageNum
     * @param pageSize
     * @return
     */


    @PostMapping("/getChatMessage")
    public R getChatMessage(@RequestParam(value = "sendUserId") String sendUserId,
                                     @RequestParam(value = "acceptUserId") String acceptUserId,
                                     @RequestParam(value = "pageNum") int pageNum,
                                     @RequestParam(value = "pageSize") int pageSize) {
        return R.data(messageService.getChatMessage(sendUserId,acceptUserId,pageNum,pageSize));
    }

    /**
     * 发送消息API
     * @param chatMessage
     * @return
     */

    @PostMapping("sendMsg")
    public R sendMsg(@RequestBody ChatMessage chatMessage) {
        Map<String,Object> repData = messageService.sendMsg(chatMessage);
        if ((Boolean) repData.get("status"))
            return R.data(repData.get("returnMsg"));
        return R.fail("发送失败");
    }

    /**
     * 发送聊天文件API
     * @param sendUserId
     * @param acceptUserId
     * @param type
     * @param time
     * @param file
     * @return
     */

    @PostMapping("/sendFileMsg")
    public R sendFileMsg(@RequestParam(value = "sendUserId") String sendUserId,
                                  @RequestParam(value = "acceptUserId") String acceptUserId,
                                  @RequestParam(value = "type") String type,
                                  @RequestParam(value = "time", required = false) Integer time,
                                  @RequestParam(value = "file")MultipartFile file
    ) {
        Map<String,Object> resData = messageService.sendFileMsg(sendUserId,acceptUserId,type,time,file);
        if ((Boolean) resData.get("status"))
            return R.data(resData.get("returnMsg"));
        else
            return R.fail("发送失败！");
    }

    /**
     * 已读消息API
     * @param sendUserId
     * @param acceptUserId
     * @return
     */

    @GetMapping("/readedMsg")
    public R readedMsg(@RequestParam("sendUserId") String sendUserId,
                                @RequestParam("acceptUserId") String acceptUserId) {
        if (messageService.readedMsg(sendUserId,acceptUserId))
            return R.success("已读成功");
        return R.fail("已读失败");
    }

    /**
     * 获取所有消息未读数API
     * @param userId
     * @return
     */

    @GetMapping("/getAllNoReadMsgNum")
    public R getAllNoReadMsgNum(@RequestParam("userId") String userId) {
        return R.data(messageService.getAllNoReadMsgNum(userId));
    }


    /**
     * 删除消息
     * @param msgId
     * @return
     */

    @DeleteMapping("/delMsg")
    public R delMsg(int msgId) {
        if (messageMapper.delMsg(msgId))
            return R.success("删除成功！");
        return R.fail("删除失败！");
    }
}