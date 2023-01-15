package com.example.demo.config;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.MyUserDetails;
import com.example.demo.util.Constant;
import com.example.demo.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Component
public class MyAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private static ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        String username = myUserDetails.getUsername();
        String token = JwtUtil.createToken(username);
        //TODO 缓存到 Redis
        //TODO 把token存到Redis中
        response.setContentType("application/json;charset=utf-8");
        System.out.println("token=" + token);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.code, 200);
        jsonObject.put(Constant.success, true);
        jsonObject.put(Constant.data, token);
        jsonObject.put(Constant.msg, "操作成功");
//        response.getWriter().write(objectMapper.writeValueAsString(token));
        response.getWriter().write(jsonObject.toString());
    }
}