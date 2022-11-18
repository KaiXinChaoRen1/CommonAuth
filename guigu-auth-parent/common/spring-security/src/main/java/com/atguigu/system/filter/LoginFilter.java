package com.atguigu.system.filter;

import com.alibaba.fastjson.JSON;
import com.atguigu.common.result.Result;
import com.atguigu.common.result.ResultCodeEnum;
import com.atguigu.common.utils.JwtHelper;
import com.atguigu.common.utils.ResponseUtil;
import com.atguigu.model.vo.LoginVo;
import com.atguigu.system.custom.CustomUser;
import com.atguigu.system.service.LoginLogService;
import com.atguigu.system.utils.IpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录过滤器,继承并重写方法
 */
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private RedisTemplate redisTemplate;

    private LoginLogService loginLogService;

    /**
     * 构造方法,指定登录接口及提交方式
     */
    public LoginFilter(AuthenticationManager authenticationManager,
                            RedisTemplate redisTemplate,
                            LoginLogService loginLogService) {
        this.setAuthenticationManager(authenticationManager);
        this.setPostOnly(false);
        // 指定登录接口及提交方式，可以指定任意路径
        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/admin/system/index/login", "POST"));
        this.redisTemplate = redisTemplate;
        this.loginLogService = loginLogService;
    }

    // 获取用户名和密码，认证
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {

            //security登录流程查看2--->登录请求放行后,进入登录认证
            // 获取用户登录携带的信息
            LoginVo loginVo = new ObjectMapper().readValue(request.getInputStream(), LoginVo.class);
            //将用户名和密码信息封装成成指定对象
            String username = loginVo.getUsername();
            String password = loginVo.getPassword();
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);


            //authenticationManager委托DaoAuthenticationProvider认证
            //      DaoAuthenticationProvider-->UserDetailsService(Impl),给定username获取包含用户信息的UserDetils,
            //      然后通过PasswordEncoder对比UserDetails与authentication中的密码是否一致实现认证
            //      填充authentication对象返回给UsernamePasswordAuthenticationFilter做下面的认证成功或失败的逻辑
            AuthenticationManager authenticationManager = this.getAuthenticationManager();
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            //security登录流程查看4-->填充authentication返回
            return authentication;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 上面那个过滤器执行完根据校验是否成功,调用下面两个方法
     * 认证成功之后做的事情(生成token返回前端)
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {

        //security登录流程查看5--->登录认证成功
        // 1.获取自定义的security用户类,内有生成token所需的信息
        CustomUser customUser = (CustomUser) auth.getPrincipal();

        // (redis保存权限数据)
        redisTemplate.opsForValue().set(customUser.getUsername(),
                JSON.toJSONString(customUser.getAuthorities()));

        // 2.生成JWT token
        String id = customUser.getSysUser().getId();
        String username = customUser.getSysUser().getUsername();
        String token = JwtHelper.createToken(id, username);

        // (记录登录日志)
        loginLogService.recordLoginLog(customUser.getUsername(), 1,
                IpUtil.getIpAddress(request), "登录成功");

        // 3.返回给前端
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        ResponseUtil.out(response, Result.ok(map));
    }

    /**
     * 认证失败的处理
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException e) throws IOException, ServletException {
        //security登录流程查看5--->登录认证失败
        if (e.getCause() instanceof RuntimeException) {
            ResponseUtil.out(response, Result.build(null, 204, e.getMessage()));
        } else {
            ResponseUtil.out(response, Result.build(null, ResultCodeEnum.LOGIN_FAILED));
        }
    }
}
