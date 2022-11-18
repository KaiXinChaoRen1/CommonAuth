package com.atguigu.system.custom;

import com.atguigu.model.system.SysUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * 自定义security用户类,继承security提供的User类
 */
public class CustomUser extends User {

    // 我们自己的用户实体对象，要调取用户信息时直接获取这个实体对象
    private SysUser SysUser;

    // 参数1,用户数据--参数2,权限数据
    public CustomUser(SysUser SysUser, Collection<? extends GrantedAuthority> authorities) {
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
