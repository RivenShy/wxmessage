package com.example.mybatplusdemo.config.websocket;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@ServerEndpoint("/websocket/{uid}")
@Component
public class WebSocketServer {

    private static int onlineCount = 0;

    private Session session;

    private String uid;

    private static final ConcurrentHashMap<Object,WebSocketServer> webSocketMap = new ConcurrentHashMap<>();


    /**
     * 连接
     * @param session
     * @param uid
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("uid") String uid) {
        this.session = session;
        this.uid = uid;
        if(webSocketMap.containsKey(uid)) {
            webSocketMap.remove(uid);
            webSocketMap.put(uid,this);
        }else {
            webSocketMap.put(uid,this);
            onlineCount++;
        }

        log.info("用户：{} 连接成功，当前在线人数：{}",uid,onlineCount);
    }

    /**
     * 关闭连接
     */
    @OnClose
    public void onClose() {
        if(webSocketMap.containsKey(uid))
            onlineCount--;
        webSocketMap.remove(uid);
        log.info("用户：{} 已退出连接，当前在线人数：{}",uid,onlineCount);
    }

    /**
     * 监听消息
     * @param content
     * @param session
     */
    @OnMessage
    public void OnMessage(String content,Session session) {

        log.info("用户：{} 发送内容：{}",uid,content);
    }

    /**
     * 服务推送消息
     * @param content
     * @throws IOException
     */
    public void sendMessage(String content) throws IOException {
        this.session.getBasicRemote().sendText(content);
    }

    /**
     * 发送消息
     * @param uid
     * @param toUid
     * @param content
     * @throws IOException
     */
    public void send(String uid,String toUid,Object content) throws IOException {
        if(webSocketMap.containsKey(toUid)) {
            Map<String,Object> msgInfo = new HashMap<>();
            msgInfo.put("sender",uid);
            msgInfo.put("acceptor",toUid);
            msgInfo.put("msg",content);
            webSocketMap.get(toUid).sendMessage(JSON.toJSONString(msgInfo));
            log.info("用户：{} 向用户： {} 发送了信息：{}",uid,toUid,content);
        }
        else {
            log.info("用户：{} 没在线",toUid);
        }
    }

}