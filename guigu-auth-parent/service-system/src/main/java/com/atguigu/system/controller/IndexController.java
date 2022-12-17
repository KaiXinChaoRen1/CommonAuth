package com.atguigu.system.controller;

import com.atguigu.common.exception.GuiguException;
import com.atguigu.common.result.Result;
import com.atguigu.common.utils.JwtHelper;
import com.atguigu.common.utils.MD5;
import com.atguigu.model.system.SysUser;
import com.atguigu.model.vo.LoginVo;

import com.atguigu.system.service.SysUserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "用户登录接口")
@RestController
@RequestMapping("/admin/system/index")
public class IndexController {

    @Autowired
    private SysUserService SysUserService;

//    @PostMapping("login")
//    public Result login(@RequestBody LoginVo loginVo) {
//
//        // 1.根据username查询是否有用户
//        SysUser SysUser = SysUserService.getUserInfoByUserName(loginVo.getUsername());
//        if (SysUser == null) {
//            throw new GuiguException(20001, "用户不存在");
//        }
//        // 2.判断密码是否一致
//        String password = loginVo.getPassword();
//        String md5Password = MD5.encrypt(password);
//        if (!SysUser.getPassword().equals(md5Password)) {
//            throw new GuiguException(20001, "密码不正确");
//        }
//        // 判断用户状态-是否被禁用
//        if (SysUser.getStatus().intValue() == 0) {
//            throw new GuiguException(20001, "用户已经被禁用");
//        }
//        // 成功登录->根据userid和username生成JWT字符串
//        String token = JwtHelper.createToken(SysUser.getId(), SysUser.getUsername());
//        // 返回
//        Map<String, Object> map = new HashMap<>();
//        map.put("token", token);
//        return Result.ok(map);
//
//
//    }

    /**
     * 根据请求头里的JWT获取用户详细信息
     */
    @GetMapping("info")
    public Result info(HttpServletRequest request) {

        String token = request.getHeader("token");
        String username = JwtHelper.getUsername(token);
        // 根据用户名称获取用户信息（基本信息 和 菜单权限 和 按钮权限数据）
        Map<String, Object> map = SysUserService.getUserInfo(username);
        return Result.ok(map);

    }

}
