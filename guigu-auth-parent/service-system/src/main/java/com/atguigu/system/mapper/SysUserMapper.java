package com.atguigu.system.mapper;

import com.atguigu.model.system.MySystemUser;
import com.atguigu.model.vo.MySystemUserQueryVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author atguigu
 * @since 2022-09-28
 */
public interface MySystemUserMapper extends BaseMapper<MySystemUser> {

    IPage<MySystemUser> selectPage(Page<MySystemUser> pageParam, @Param("vo") MySystemUserQueryVo MySystemUserQueryVo);
}
