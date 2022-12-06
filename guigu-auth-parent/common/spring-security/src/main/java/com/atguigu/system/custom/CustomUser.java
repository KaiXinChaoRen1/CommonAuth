package com.atguigu.system.custom;


import com.atguigu.model.system.SysUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * 自定义security用户类,继承security提供的User类
 *      封装用户信息
 *          和权限信息
 */
public class CustomUser extends User {

    /**
     * 我们的用户实体属性
     */
    private SysUser SysUser;

    /**
     * 构造
     * @param SysUser           用户数据
     * @param authorities       权限数据
     */
    public CustomUser(com.atguigu.model.system.SysUser SysUser, Collection<? extends GrantedAuthority> authorities) {
        super(SysUser.getUsername(), SysUser.getPassword(), authorities);
        this.SysUser = SysUser;
    }





    public SysUser getSysUser() {
        return SysUser;
    }
    public void setSysUser(SysUser SysUser) {
        this.SysUser = SysUser;
    }
}
