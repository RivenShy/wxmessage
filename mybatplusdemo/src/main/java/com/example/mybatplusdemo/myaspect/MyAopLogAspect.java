package com.example.mybatplusdemo.myaspect;

import com.example.mybatplusdemo.annotation.MyAopLogAnnotation;
import com.example.mybatplusdemo.utils.IpUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class MyAopLogAspect {

    private static final Logger log = LoggerFactory.getLogger(MyAopLogAspect.class);

    private static final ThreadLocal<Long> TIME_THREADLOCAL = new NamedThreadLocal<>("cost time");

    @Before(value = "@annotation(controllerLog)")
    public void before(JoinPoint joinPoint, MyAopLogAnnotation controllerLog) {
        TIME_THREADLOCAL.set(System.currentTimeMillis());
    }

    @AfterReturning(pointcut = "@annotation(controllerLog)", returning = "jsonResult")
    public void afterReturning(JoinPoint joinPoint, MyAopLogAnnotation controllerLog, Object jsonResult) {
        handleLog(joinPoint, controllerLog, null, jsonResult);
    }

    protected void handleLog(final JoinPoint joinPoint, MyAopLogAnnotation controllerLog, final Exception e, Object jsonResult) {
        try {
            Long time = System.currentTimeMillis() - TIME_THREADLOCAL.get();
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) attributes;
            assert requestAttributes != null;
            HttpServletRequest httpServletRequest = requestAttributes.getRequest();
            Map<?, ?> paramsMap = getParametersMap(httpServletRequest);
            // 请求的ip地址
            String ip = IpUtils.getIpAddr();
            log.info("请求地址：{}，请求方法：{}，请求参数：{}，返回值：{}，耗时：{}毫秒", httpServletRequest.getRequestURL(), className + "." + methodName, paramsMap, jsonResult, time);
        } catch (Exception exp) {
            log.error("异常信息:{}", exp.getMessage());
        } finally {
            TIME_THREADLOCAL.remove();
        }
    }

    public static Map<String, String> getParametersMap(ServletRequest request) {
        Map<String, String> params = new HashMap<>();
        for(Map.Entry<String, String[]> entry : getParams(request).entrySet()) {
            params.put(entry.getKey(), StringUtils.join(entry.getValue()));
        }
        return params;
    }

    public static Map<String, String[]> getParams(ServletRequest request) {
        Map<String, String[]> map = request.getParameterMap();
        return Collections.unmodifiableMap(map);
    }
}
