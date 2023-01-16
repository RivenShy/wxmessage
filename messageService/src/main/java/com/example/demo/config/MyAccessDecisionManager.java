package com.example.demo.config;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
@Component
public class MyAccessDecisionManager implements AccessDecisionManager {
    /**
     *
     * @param authentication    当前登录用户，可以获取用户的权限列表
     * @param object            FilterInvocation对象，可以获取请求url
     * @param configAttributes
     * @throws AccessDeniedException
     * @throws InsufficientAuthenticationException
     */
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        String requestUrl = ((FilterInvocation) object).getRequestUrl();
        System.out.println(requestUrl);
        //  当前用户拥有的权限（能访问的资源）
        Collection<? extends GrantedAuthority> grantedAuthorities = authentication.getAuthorities();
        List<String> authorities = grantedAuthorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
	 /*if (!authorities.contains(requestUrl)) {
		 throw new AccessDeniedException("权限不足");
	 }*/
        //  判断访问当前资源所需要的权限用户是否拥有
        //  PS: 在我看来，其实就是看两个集合是否有交集
        for (ConfigAttribute configAttribute : configAttributes) {
            String attr = configAttribute.getAttribute();
            if ("ROLE_login".equals(attr)) {
                if (authentication instanceof AnonymousAuthenticationToken) {
                    throw new AccessDeniedException("非法请求");
                }
            }
            if (authorities.contains(attr)) {
                return;
            }
        }
        throw new AccessDeniedException("权限不足");
    }
    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }
    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}