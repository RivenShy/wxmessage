//package com.example.demo.config;
//
//import com.example.demo.entity.SysPermission;
//import com.example.demo.mapper.SysPermissionRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.ConfigAttribute;
//import org.springframework.security.access.SecurityConfig;
//import org.springframework.security.web.FilterInvocation;
//import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
//import org.springframework.stereotype.Component;
//import org.springframework.util.AntPathMatcher;
//import org.springframework.util.CollectionUtils;
//import java.util.Collection;
//import java.util.List;
//import java.util.stream.Collectors;
//@Component
//public class MyFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {
//    private AntPathMatcher pathMatcher = new AntPathMatcher();
//    @Autowired
//    private SysPermissionRepository sysPermissionRepository;
//    @Override
//    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
//        String requestUrl = ((FilterInvocation) object).getRequestUrl();
//        //  查找与当前请求URL匹配的所有权限
//        List<SysPermission> sysPermissionList = sysPermissionRepository.findAll();
//        List<String> urls = sysPermissionList.stream()
//                .map(SysPermission::getUrl)
//                .filter(e->pathMatcher.match(e, requestUrl))
//                .distinct()
//                .collect(Collectors.toList());
//        if (!CollectionUtils.isEmpty(urls)) {
//            return SecurityConfig.createList(urls.toArray(new String[urls.size()]));
//        }
//        return SecurityConfig.createList("ROLE_login");
//    }
//    @Override
//    public Collection<ConfigAttribute> getAllConfigAttributes() {
//        return null;
//    }
//    @Override
//    public boolean supports(Class<?> clazz) {
//        return true;
//    }
//}