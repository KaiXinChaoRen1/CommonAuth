package com.atguigu.system.test;


import com.atguigu.model.system.SysRole;
import com.atguigu.model.vo.SysRoleQueryVo;
import com.atguigu.system.CommonAuthApplication;
import com.atguigu.system.service.LoginLogService;
import com.atguigu.system.service.SysRoleService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= CommonAuthApplication.class)
public class LwqTest {
    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private LoginLogService loginLogService;

    @Test
    public void name1(){
        SysRoleQueryVo sysRoleQueryVo = new SysRoleQueryVo();
        sysRoleQueryVo.setRoleName("管理员");
        Page<SysRole> pageParam = new Page<>(1L, 1L);
        IPage<SysRole> sysRoleIPage = sysRoleService.selectPage(pageParam, sysRoleQueryVo);
        System.out.println(sysRoleIPage.getRecords());
        System.out.println(sysRoleIPage.getCurrent());//当前页
        System.out.println(sysRoleIPage.getPages());//总页数
        System.out.println(sysRoleIPage.getSize());//每页显示
        System.out.println(sysRoleIPage.getTotal());//总数据量


    }
}
