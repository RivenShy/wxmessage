package com.example.demo.config;

import com.example.demo.service.impl.MyUserDetailsService;
import com.example.demo.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 * 负责在每次请求中，解析请求头中的token，从中取得用户信息，生成认证对象传递给下一个过滤器
 * @Author ChengJianSheng
 * @Date 2021/5/7
 */
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
    private MyUserDetailsService myUserDetailsService;
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, MyUserDetailsService myUserDetailsService) {
        super(authenticationManager);
        this.myUserDetailsService = myUserDetailsService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = request.getHeader("token");
//        System.out.println("请求头中带的token: " + token);
        if (StringUtils.isNoneBlank(token)) {
            if (!JwtUtil.isTokenExpired(token)) {
                String username = JwtUtil.extractUsername(token);
                if (StringUtils.isNoneBlank(username) && null == SecurityContextHolder.getContext().getAuthentication()) {
                    //  查询用户权限，有以下三种方式:
                    //  1. 可以从数据库中加载
                    //  2. 可以从Redis中加载（PS: 前提是之前已经缓存到Redis中了）
                    //  3. 可以从token中加载（PS: 前提是生成token的时候把用户权限作为Claims放置其中了）
                    UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authRequest.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authRequest);
                }
            }
        }
        chain.doFilter(request, response);
    }
}