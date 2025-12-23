package com.example.mybatplusdemo.config.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import java.util.UUID;

/**
 * @Title: WebSocketConfig
 * @Package: com.jiayuan.common.config
 * @Description: websocket配置
 * @Author: xmc
 * @Date: 创建时间 2024-04-24
 */

// todo 运行SpringbootTest,暂时注释
//@Configuration
public class WebSocketConfig {

    public static final String WEBSOCKET_SERVER_ID = UUID.randomUUID().toString();

    /**
     * 自动注册使用了@ServerEndpoint注解声明的Websocket endpoint
     *
     * @return
     */

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    /**
     * 通信文本消息和二进制缓存区大小
     * 避免对接 第三方 报文过大时，Websocket 1009 错误
     *
     * @return
     */

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        // 在此处设置bufferSize
        container.setMaxTextMessageBufferSize(10240000);
        container.setMaxBinaryMessageBufferSize(10240000);
        container.setMaxSessionIdleTimeout(15 * 60000L);
        return container;
    }
}

