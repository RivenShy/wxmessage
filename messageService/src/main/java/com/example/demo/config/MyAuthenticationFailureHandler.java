package com.example.demo.config;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.util.Constant;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Component
public class MyAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private static ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        exception.printStackTrace();
        response.setContentType("application/json;charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.code, 403);
        jsonObject.put(Constant.success, false);
        jsonObject.put(Constant.data, null);
        jsonObject.put(Constant.msg, "登录失败，请检查用户名和密码");
//        response.getWriter().write(objectMapper.writeValueAsString("error"));
        response.getWriter().write(jsonObject.toString());
    }
}