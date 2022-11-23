package com.example.demo.service.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
@Component("myAccessDecisionService")
public class MyAccessDecisionService {
    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
//        System.out.println("进来了MyAccessDecisionService.hasPermission()");
        // 未配置角色,先允许访问,注意：return true为所有用户都可以访问所有资源，包括未登录用户
        if(true) {
            return true;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(request.getRequestURI());
            return userDetails.getAuthorities().contains(simpleGrantedAuthority);
        }
        System.out.println("进来了MyAccessDecisionService.hasPermission() return false");
        return false;
    }
}