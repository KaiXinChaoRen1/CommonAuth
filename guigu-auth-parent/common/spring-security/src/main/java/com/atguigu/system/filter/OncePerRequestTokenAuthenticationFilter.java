package com.atguigu.system.filter;

import com.alibaba.fastjson.JSON;
import com.atguigu.common.result.Result;
import com.atguigu.common.result.ResultCodeEnum;
import com.atguigu.common.utils.JwtHelper;
import com.atguigu.common.utils.ResponseUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 认证解析过滤器
 *      每一次请求都进行的处理
 */
public class OncePerRequestTokenAuthenticationFilter extends OncePerRequestFilter {

    private RedisTemplate redisTemplate;

    public OncePerRequestTokenAuthenticationFilter(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        logger.info("uri:" + request.getRequestURI());

        //如果是访问登录接口，直接放行
        if ("/admin/system/index/login".equals(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        if ("/prod-api/admin/system/index/login".equals(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        //不是登录接口需要验证token,从redis取出全部信息
        UsernamePasswordAuthenticationToken authentication = this.getAuthentication(request);
        if (null != authentication) {
            //放行
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } else {
            //根据token获取不到正确信息或者没有token,直接拒绝访问
            ResponseUtil.out(response, Result.build(null, ResultCodeEnum.PERMISSION));
        }
    }

    //验证token,取出redis中用户的所有信息
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader("token");
        logger.info("token:" + token);
        if (!StringUtils.isEmpty(token)) {
            String userName = JwtHelper.getUsername(token);
            logger.info("userName:" + userName);
            if (!StringUtils.isEmpty(userName)) {
                String authoritiesString =
                        (String) redisTemplate.opsForValue().get(userName);
                List<Map> mapList = JSON.parseArray(authoritiesString, Map.class);
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                for (Map map : mapList) {
                    authorities.add(new SimpleGrantedAuthority((String) map.get("authority")));
                }
                return new UsernamePasswordAuthenticationToken(userName, null, authorities);
            }
        }
        return null;
    }
}
