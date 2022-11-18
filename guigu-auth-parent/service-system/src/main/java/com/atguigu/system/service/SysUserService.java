package com.atguigu.system.service;

import com.atguigu.model.system.MySystemUser;
import com.atguigu.model.vo.MySystemUserQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author atguigu
 * @since 2022-09-28
 */
public interface MySystemUserService extends IService<MySystemUser> {

    //用户列表
    IPage<MySystemUser> selectPage(Page<MySystemUser> pageParam, MySystemUserQueryVo MySystemUserQueryVo);

    //更改用户状态
    void updateStatus(String id, Integer status);

    //username查询
    MySystemUser getUserInfoByUserName(String username);

    //根据用户名称获取用户信息（基本信息 和 菜单权限 和 按钮权限数据）
    Map<String, Object> getUserInfo(String username);
}
