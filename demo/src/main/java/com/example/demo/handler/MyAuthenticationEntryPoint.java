package com.example.demo.handler;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.util.Constant;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 * 未认证（未登录）统一处理
 * @Author ChengJianSheng
 * @Date 2021/5/7
 */
@Component
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.code, 403);
        jsonObject.put(Constant.success, false);
        jsonObject.put(Constant.data, null);
        jsonObject.put(Constant.msg, "未登录，请先登录");
        response.getWriter().write(jsonObject.toString());
    }
}