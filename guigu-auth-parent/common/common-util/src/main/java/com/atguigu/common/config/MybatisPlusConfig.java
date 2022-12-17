//package com.atguigu.common.config;
//
//
//import com.baomidou.mybatisplus.annotation.DbType;
//import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
//import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
//import org.mybatis.spring.annotation.MapperScan;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
///**
// * 配置Mybatis-Plus插件,为什么这里不生效,放到启动类里就生效了?艹
// */
//@Configuration
//@MapperScan("com.atguigu.system.mapper")
//public class MybatisPlusConfig {
//
//    @Bean
//    public MybatisPlusInterceptor addPaginationInnerInterceptor(){
//        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
//        //向Mybatis过滤器链中添加分页拦截器
//        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
//        return interceptor;
//    }
//}
