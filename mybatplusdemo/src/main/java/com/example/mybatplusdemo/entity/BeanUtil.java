package com.example.mybatplusdemo.entity;

import com.example.mybatplusdemo.utils.RabbitBeanCopier;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.core.Converter;
import org.springframework.lang.Nullable;

public class BeanUtil extends BeanUtils {

//    public BeanUtil() {
//    }
//
//    @Nullable
//    public static <T> T copy(@Nullable Object source, Class<T> clazz) {
//        return source == null ? null : copy(source, source.getClass(), clazz);
//    }
//
//    @Nullable
//    public static <T> T copy(@Nullable Object source, Class sourceClazz, Class<T> targetClazz) {
//        if (source == null) {
//            return null;
//        } else {
//            RabbitBeanCopier copier = RabbitBeanCopier.create(sourceClazz, targetClazz, false);
//            T to = newInstance(targetClazz);
//            copier.copy(source, to, (Converter)null);
//            return to;
//        }
//    }
//
//    public static <T> T newInstance(Class<?> clazz) {
//        return (T) instantiateClass(clazz);
//    }
}
