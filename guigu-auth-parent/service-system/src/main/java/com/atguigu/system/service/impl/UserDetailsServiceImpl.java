package com.atguigu.system.service.impl;

import com.atguigu.model.system.SysUser;
import com.atguigu.system.custom.CustomUser;
import com.atguigu.system.service.SysMenuService;
import com.atguigu.system.service.SysUserService;
import com.atguigu.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * security-用户信息查询
 */
@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysMenuService sysMenuService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //security登录流程查看3--->
        SysUser SysUser = sysUserService.getUserInfoByUserName(username);
        if (SysUser == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        if (SysUser.getStatus().intValue() == 0) {
            throw new RuntimeException("用户被禁用了");
        }
        // 根据userid查询操作权限数据
        List<String> userPermsList = sysMenuService.getUserButtonList(SysUser.getId());
        // 转换security要求格式数据
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (String perm : userPermsList) {
            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(perm.trim());
            authorities.add(simpleGrantedAuthority);
        }

        return new CustomUser(SysUser, authorities);
    }
}
