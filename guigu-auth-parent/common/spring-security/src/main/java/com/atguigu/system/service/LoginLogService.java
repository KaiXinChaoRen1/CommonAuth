package com.atguigu.system.service;


import com.atguigu.model.system.SysLoginLog;
import com.atguigu.model.vo.SysLoginLogQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 登录日志接口,如果这个接口卸载service-system模块,maven再引入时会产生maven的循环依赖
 */
public interface LoginLogService {

    /**
     * 记录登录日志
     */
    public void recordLoginLog(String username,Integer status,
                               String ipaddr,String message);

    //条件分页查询登录日志
    IPage<SysLoginLog> selectPage(long page, long limit, SysLoginLogQueryVo sysLoginLogQueryVo);
}
