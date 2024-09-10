package com.example.mybatplusdemo.myaspect;


import cn.hutool.core.collection.ListUtil;
import com.example.mybatplusdemo.annotation.MyDataScope;
import com.example.mybatplusdemo.config.Func;
import com.example.mybatplusdemo.dto.BaseQueryDTO;
import com.example.mybatplusdemo.utils.CollectionUtilsPan;
import com.example.mybatplusdemo.utils.beanUtil.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Aspect
@Component
public class MyDataScopeAspect {

    @Around("@annotation(com.example.mybatplusdemo.annotation.MyDataScope)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        if (Func.isNull(methodSignature)) {
            return point.proceed();
        }
        // 方法参数
        Object[] params = point.getArgs();
        MyDataScope annotation = methodSignature.getMethod().getAnnotation(MyDataScope.class);
        if (Func.isNull(annotation) || Func.isEmpty(params)) {
            return point.proceed();
        }
        BaseQueryDTO baseQueryDto = new BaseQueryDTO();
        baseQueryDto.setScopeSalesCode("1");
        baseQueryDto.setScopeOrganizationIds(new ArrayList<>());
        baseQueryDto.setScopeTeamAgencyIds(new ArrayList<>());
        before(BeanUtil.toMap(baseQueryDto), params);
        return point.proceed();
    }

    private void before(Map<String, Object> baseQueryDtoMap, Object[] params) {
        try {
            for (Object param : params) {
                if (!(param instanceof BaseQueryDTO)) {
                    continue;
                }
                Class<?> baseQueryClass = getBaseQueryClass(param.getClass());
                Map<String, Field> filedMap = CollectionUtilsPan.toMap(ListUtil.toList(baseQueryClass.getDeclaredFields()),
                        Field::getName, Function.identity());
                for (Map.Entry<String, Object> baseQueryDtoEntry : baseQueryDtoMap.entrySet()) {
                    String filedName = baseQueryDtoEntry.getKey();
                    Object value = baseQueryDtoEntry.getValue();
                    if (Func.isEmpty(value)) {
                        continue;
                    }
                    Field field = filedMap.get(filedName);
                    field.setAccessible(true);
                    field.set(param, value);
                }
            }
        } catch (Throwable e) {
            log.error("执行前置方法失败！", e);
        }
    }

    private Class<?> getBaseQueryClass(Class<?> clz) {
        if (clz == BaseQueryDTO.class || clz == Object.class) {
            return clz;
        }
        return getBaseQueryClass(clz.getSuperclass());
    }
}
