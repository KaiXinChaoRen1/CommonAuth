package com.atguigu.system.aspect;

import com.alibaba.fastjson.JSON;
import com.atguigu.common.utils.IpUtil;
import com.atguigu.common.utils.JwtHelper;
import com.atguigu.model.system.SysOperLog;
import com.atguigu.system.annotation.Log;
import com.atguigu.system.service.OperLogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Map;

/**
 * AOP+注解
 * 实现记录操作日志
 */

@Aspect
@Component
@Slf4j
public class LogAspect {

    @Autowired
    private OperLogService operLogService;

    @AfterReturning(pointcut = "@annotation(controllerLog)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Log controllerLog, Object jsonResult) {
        handleLog(joinPoint, controllerLog, null, jsonResult);
    }

    protected void handleLog(final JoinPoint joinPoint, Log controllerLog, final Exception e, Object jsonResult) {
        try {
            RequestAttributes ra = RequestContextHolder.getRequestAttributes();
            ServletRequestAttributes sra = (ServletRequestAttributes) ra;
            HttpServletRequest request = sra.getRequest();

            //创建操作日志记录对象
            SysOperLog operLog = new SysOperLog();
            //set请求的地址
            String ip = IpUtil.getIpAddress(request);
            operLog.setOperIp(ip);
            operLog.setOperUrl(request.getRequestURI());
            //set状态
            operLog.setStatus(1);
            //set用户名
            String token = request.getHeader("token");
            String userName = JwtHelper.getUsername(token);
            operLog.setOperName(userName);
            //set错误信息
            if (e != null) {
                operLog.setStatus(0);
                operLog.setErrorMsg(e.getMessage());
            }
            // set方法名称
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            operLog.setMethod(className + "." + methodName + "()");

            // set请求方式
            operLog.setRequestMethod(request.getMethod());

            // set设置注解上的参数
            this.getControllerMethodDescription(joinPoint, controllerLog, operLog, jsonResult);

            // 保存数据库
            operLogService.saveSysLog(operLog);
        } catch (Exception exp) {
            log.error("记录操作日志");
        }
    }

    /**
     * set设置注解里的参数
     * 下面四个方法,一个调一个
     */
    public void getControllerMethodDescription(JoinPoint joinPoint, Log log, SysOperLog operLog, Object jsonResult) throws Exception {
        // set标题
        operLog.setTitle(log.title());
        // set业务类型(新增,修改,删除等)
        operLog.setBusinessType(log.businessType().name());
        // 设置操作人类别(后台用户,手机用户等)
        operLog.setOperatorType(log.operatorType().name());
        // 是否需要保存request，参数和值
        if (log.isSaveRequestData()) {
            setRequestValue(joinPoint, operLog);
        }
        // 是否需要保存response，参数和值
        if (log.isSaveResponseData() && !StringUtils.isEmpty(jsonResult)) {
            operLog.setJsonResult(JSON.toJSONString(jsonResult));
        }
    }

    /**
     * 获取请求的参数，放到log中
     */
    private void setRequestValue(JoinPoint joinPoint, SysOperLog operLog) throws Exception {
        String requestMethod = operLog.getRequestMethod();
        if (HttpMethod.PUT.name().equals(requestMethod) || HttpMethod.POST.name().equals(requestMethod)) {
            String params = argsArrayToString(joinPoint.getArgs());
            operLog.setOperParam(params);
        }
    }

    /**
     * 参数拼装
     */
    private String argsArrayToString(Object[] paramsArray) {
        String params = "";
        if (paramsArray != null && paramsArray.length > 0) {
            for (Object o : paramsArray) {
                if (!StringUtils.isEmpty(o) && !isFilterObject(o)) {
                    try {
                        Object jsonObj = JSON.toJSON(o);
                        params += jsonObj.toString() + " ";
                    } catch (Exception e) {
                    }
                }
            }
        }
        return params.trim();
    }

    /**
     * 判断是否需要过滤的对象。
     *
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    @SuppressWarnings("rawtypes")
    public boolean isFilterObject(final Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection collection = (Collection) o;
            for (Object value : collection) {
                return value instanceof MultipartFile;
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map map = (Map) o;
            for (Object value : map.entrySet()) {
                Map.Entry entry = (Map.Entry) value;
                return entry.getValue() instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse
                || o instanceof BindingResult;
    }
}
