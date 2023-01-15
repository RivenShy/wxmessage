//package com.example.demo.service.impl;
//
//import com.example.demo.entity.*;
//import com.example.demo.mapper.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Component
//public class MyUserDetailsServiceBak implements UserDetailsService {
//    @Autowired
//    private SysUserRepository sysUserRepository;
//    @Autowired
//    private SysRoleRepository sysRoleRepository;
//    @Autowired
//    private SysUserRoleRelationRepository sysUserRoleRelationRepository;
//    @Autowired
//    private SysRolePermissionRelationRepository sysRolePermissionRelationRepository;
//    @Autowired
//    private SysPermissionRepository sysPermissionRepository;
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        //  查用户
//        Optional<SysUser> optionalSysUser = sysUserRepository.findByUsername(username);
//        SysUser sysUser = optionalSysUser.orElseThrow(()->new UsernameNotFoundException("用户名" + username + "不存在"));
//        //  查权限
//        List<SysUserRoleRelation> sysUserRoleRelationList = sysUserRoleRelationRepository.findByUserId(sysUser.getId());
//        List<Integer> roleIds = sysUserRoleRelationList.stream().map(SysUserRoleRelation::getRoleId).collect(Collectors.toList());
//        List<SysRole> sysRoleList = sysRoleRepository.findByIdIn(roleIds);
//        List<SysRolePermissionRelation> sysRolePermissionRelationList = sysRolePermissionRelationRepository.findByRoleIdIn(roleIds);
//        List<Integer> permissionIds = sysRolePermissionRelationList.stream().map(SysRolePermissionRelation::getPermissionId).collect(Collectors.toList());
//        List<SysPermission> sysPermissionList = sysPermissionRepository.findByIdIn(permissionIds);
//        List<GrantedAuthority> grantedAuthorities = new ArrayList<>(sysPermissionList.size());
//        for (SysPermission permission : sysPermissionList) {
//            grantedAuthorities.add(new SimpleGrantedAuthority(permission.getUrl()));
//        }
//        sysRoleList.forEach(role->grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleCode())));
//        MyUserDetails myUserDetails = new MyUserDetails(sysUser.getUsername(), sysUser.getPassword(), sysUser.isEnabled());
//        myUserDetails.setAuthorities(grantedAuthorities);
//        return myUserDetails;
//    }
//}