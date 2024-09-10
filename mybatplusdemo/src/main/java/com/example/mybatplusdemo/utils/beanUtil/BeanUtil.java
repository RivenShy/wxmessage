package com.example.mybatplusdemo.utils.beanUtil;

import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

public class BeanUtil {

    public static Map<String, Object> toMap(@Nullable Object bean) {
        return (Map)(bean == null ? new HashMap(0) : RabbitBeanMap.create(bean));
    }
}
