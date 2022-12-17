package com.atguigu.system.controller;

import com.atguigu.common.result.Result;
import com.atguigu.model.system.SysLoginLog;
import com.atguigu.model.system.SysOperLog;
import com.atguigu.model.vo.SysLoginLogQueryVo;
import com.atguigu.model.vo.SysOperLogQueryVo;
import com.atguigu.system.service.LoginLogService;
import com.atguigu.system.service.OperLogService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "系统日志查看")
@RestController
@RequestMapping(value = "/admin/system")
public class LogController {

    @Autowired
    private LoginLogService loginLogService;

    @Autowired
    private OperLogService operLogService;

    @ApiOperation(value = "查询登录日志")
    @GetMapping("/loginLog/{page}/{limit}")
    public Result index(@PathVariable long page,
                        @PathVariable long limit,
                        SysLoginLogQueryVo sysLoginLogQueryVo) {

        IPage<SysLoginLog> pageModel = loginLogService.selectPage(page, limit, sysLoginLogQueryVo);
        return Result.ok(pageModel);
    }


    @ApiOperation(value = "查询操作日志")
    @GetMapping("/OperationLog/{page}/{limit}")
    public Result index(
            @ApiParam(name = "page", value = "当前页码", required = true) @PathVariable Long page,
            @ApiParam(name = "limit", value = "每页记录数", required = true) @PathVariable Long limit,
            @ApiParam(name = "sysOperLogVo", value = "查询对象", required = false) SysOperLogQueryVo sysOperLogQueryVo) {

        IPage<SysOperLog> pageModel = operLogService.selectPage(page, limit, sysOperLogQueryVo);
        return Result.ok(pageModel.getRecords());
    }
}
