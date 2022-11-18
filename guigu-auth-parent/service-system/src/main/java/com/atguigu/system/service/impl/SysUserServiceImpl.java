package com.atguigu.system.service.impl;

import com.atguigu.model.system.MySystemUser;
import com.atguigu.model.vo.RouterVo;
import com.atguigu.model.vo.MySystemUserQueryVo;
import com.atguigu.system.mapper.MySystemUserMapper;
import com.atguigu.system.service.SysMenuService;
import com.atguigu.system.service.MySystemUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2022-09-28
 */
@Service
public class MySystemUserServiceImpl extends ServiceImpl<MySystemUserMapper, MySystemUser> implements MySystemUserService {

    @Autowired
    private SysMenuService sysMenuService;

    //用户列表
    @Override
    public IPage<MySystemUser> selectPage(Page<MySystemUser> pageParam, MySystemUserQueryVo MySystemUserQueryVo) {
        return baseMapper.selectPage(pageParam,MySystemUserQueryVo);
    }

    //更改用户状态
    @Override
    public void updateStatus(String id, Integer status) {
        //根据用户id查询
        MySystemUser MySystemUser = baseMapper.selectById(id);
        //设置修改状态
        MySystemUser.setStatus(status);
        //调用方法修改
        baseMapper.updateById(MySystemUser);
    }

    //username查询
    @Override
    public MySystemUser getUserInfoByUserName(String username) {
        QueryWrapper<MySystemUser> wrapper = new QueryWrapper<>();
        wrapper.eq("username",username);
        return baseMapper.selectOne(wrapper);
    }

    //根据用户名称获取用户信息（基本信息 和 菜单权限 和 按钮权限数据）
    @Override
    public Map<String, Object> getUserInfo(String username) {
        //根据username查询用户基本信息
        MySystemUser MySystemUser = this.getUserInfoByUserName(username);
        //根据userid查询菜单权限值
        List<RouterVo> routerVolist = sysMenuService.getUserMenuList(MySystemUser.getId());
        //根据userid查询按钮权限值
        List<String> permsList = sysMenuService.getUserButtonList(MySystemUser.getId());

        Map<String,Object> result = new HashMap<>();
        result.put("name",username);
        result.put("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        result.put("roles","[\"admin\"]");
        //菜单权限数据
        result.put("routers",routerVolist);
        //按钮权限数据
        result.put("buttons",permsList);
        return result;
    }
}
