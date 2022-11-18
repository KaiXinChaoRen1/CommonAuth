package com.atguigu.system.controller;


import com.atguigu.common.result.Result;
import com.atguigu.common.utils.MD5;
import com.atguigu.model.system.MySystemUser;
import com.atguigu.model.vo.MySystemUserQueryVo;
import com.atguigu.system.service.MySystemUserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Api(tags = "用户管理接口")
@RestController
@RequestMapping("/admin/system/MySystemUser")
public class MySystemUserController {

    @Autowired
    private MySystemUserService MySystemUserService;

    @ApiOperation("更改用户状态(0-停用,1-正常使用)")
    @GetMapping("updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable String id,
                               @PathVariable Integer status) {
        MySystemUserService.updateStatus(id, status);
        return Result.ok();
    }

    //********************************基础CRUD*******************************

    @ApiOperation("用户列表(条件分页)")
    @GetMapping("/{page}/{limit}")
    public Result list(@PathVariable Long page,
                       @PathVariable Long limit,
                       MySystemUserQueryVo MySystemUserQueryVo) {
        Page<MySystemUser> pageParam = new Page<>(page, limit);
        IPage<MySystemUser> pageModel = MySystemUserService.selectPage(pageParam, MySystemUserQueryVo);
        return Result.ok(pageModel);
    }

    /**
     *  用户密码加密存储
     */
    @ApiOperation("添加用户")
    @PostMapping("save")
    public Result save(@RequestBody MySystemUser user) {
        //密码进行MD5加密
        String encryptedPassword = MD5.encrypt(user.getPassword());
        user.setPassword(encryptedPassword);
        boolean is_Success = MySystemUserService.save(user);
        if (is_Success) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    @ApiOperation("根据id查询")
    @GetMapping("getUser/{id}")
    public Result getUser(@PathVariable String id) {
        MySystemUser user = MySystemUserService.getById(id);
        return Result.ok(user);
    }

    @ApiOperation("修改用户")
    @PostMapping("update")
    public Result update(@RequestBody MySystemUser user) {
        boolean is_Success = MySystemUserService.updateById(user);
        if (is_Success) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    @ApiOperation("删除用户")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable String id) {
        boolean is_Success = MySystemUserService.removeById(id);
        if (is_Success) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }
}

