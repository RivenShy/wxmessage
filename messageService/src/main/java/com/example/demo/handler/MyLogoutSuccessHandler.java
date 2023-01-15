package com.example.demo.handler;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.util.Constant;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MyLogoutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.code, 200);
        jsonObject.put(Constant.success, true);
        jsonObject.put(Constant.data, null);
        jsonObject.put(Constant.msg, "退出登录成功");
        // 退出登录成功，前端清理一下保存的token
        response.getWriter().write(jsonObject.toString());
    }
}
