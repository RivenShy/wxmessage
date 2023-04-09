package com.security.demo.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.security.demo.entity.LoginUser;
import com.security.demo.entity.User;
import com.security.demo.mapper.MenuMapper;
import com.security.demo.mapper.UserMapper;
import com.security.demo.util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @Author 三更  B站： https://space.bilibili.com/663528522
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MenuMapper menuMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        try {
//            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
//            wrapper.eq(User::getUserName,username);
//            User user = userMapper.selectOne(wrapper);
//            if(Objects.isNull(user)){
//                throw new RuntimeException("用户名或密码错误");
//            }
//            List<String> permissionKeyList =  menuMapper.selectPermsByUserId(user.getId());
////        //测试写法
////        List<String> list = new ArrayList<>(Arrays.asList("test"));
//            return new LoginUser(user,permissionKeyList);
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
        String tenantId = AuthUtils.getRequestTenantId();
        System.out.println(tenantId);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserName,username);
        User user = userMapper.selectOne(wrapper);
        if(Objects.isNull(user)){
            throw new RuntimeException("用户名或密码错误");
        }
//        List<String> permissionKeyList =  menuMapper.selectPermsByUserId(user.getId());
//        //测试写法
        List<String> list = new ArrayList<>(Arrays.asList("test"));
//        return new LoginUser(user,permissionKeyList);
        LoginUser loginUser = new LoginUser(user,list);
        List<GrantedAuthority> grantedAuthorityList = (List<GrantedAuthority>) loginUser.getAuthorities();
        System.out.println(grantedAuthorityList.size());
//        return new LoginUser(user,list);
        return loginUser;

    }
}

