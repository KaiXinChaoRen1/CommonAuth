package com.atguigu.system.controller;

import com.atguigu.common.result.Result;
import com.atguigu.common.utils.MD5;
import com.atguigu.model.system.SysUser;
import com.atguigu.model.vo.SysUserQueryVo;
import com.atguigu.system.annotation.Log;
import com.atguigu.system.service.SysUserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

@Api(tags = "用户管理接口")
@RestController
@RequestMapping("/admin/system/sysUser")
@Slf4j
public class SysUserController {

    @Autowired
    private SysUserService SysUserService;

    @ApiOperation("更改用户状态(0-停用,1-正常使用)")
    @GetMapping("updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable String id,
                               @PathVariable Integer status) {
        SysUserService.updateStatus(id, status);
        return Result.ok();
    }

    // ********************************基础CRUD*******************************
    @Log(title = "查看用户")
    @ApiOperation("用户列表(条件分页)")
    @GetMapping("/{page}/{limit}")
    public Result list(@PathVariable Long page,
                       @PathVariable Long limit,
                       SysUserQueryVo SysUserQueryVo) {
        Page<SysUser> pageParam = new Page<>(page, limit);
        IPage<SysUser> pageModel = SysUserService.selectPage(pageParam, SysUserQueryVo);
        return Result.ok(pageModel);
    }

    /**
     * 用户密码加密存储
     */
    @ApiOperation("添加用户")
    @PostMapping("save")
    public Result save(@RequestBody SysUser user) {
        // 密码进行MD5加密
        String password = user.getPassword();
        if(!StringUtils.hasText(password)){
            return Result.fail().message("密码不能没有内容");
        }
        if(StringUtils.containsWhitespace(password)){
            return Result.fail().message("密码不能包含空格");
        }
        if(password.trim().length()<6){
            return Result.fail().message("密码不能小于六位");
        }
        String encryptedPassword = MD5.encrypt(user.getPassword());
        user.setPassword(encryptedPassword);
        try {
            boolean is_Success = SysUserService.save(user);
            if (is_Success) {
                return Result.ok();
            } else {
                return Result.fail();
            }
        } catch (Exception e) {
            log.error("错误被捕获处理:",e);
            return Result.fail().message("添加用户失败");
        }
    }

    @ApiOperation("根据id查询")
    @GetMapping("getUser/{id}")
    public Result getUser(@PathVariable String id) {
        SysUser user = SysUserService.getById(id);
        return Result.ok(user);
    }

    @ApiOperation("修改用户")
    @PostMapping("update")
    public Result update(@RequestBody SysUser user) {
        boolean is_Success = SysUserService.updateById(user);
        if (is_Success) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    @ApiOperation("删除用户")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable String id) {
        boolean is_Success = SysUserService.removeById(id);
        if (is_Success) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }
}
