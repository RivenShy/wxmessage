package com.example.mybatplusdemo.entity;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class BeanUtil extends BeanUtils {

    public static <T> T copyProperties(Object source, Class<T> targetClazz) throws BeansException {
        Assert.notNull(source, "copy source can not be null");
        T to = newInstance(targetClazz);
        copyProperties(source, to);
        return to;
    }

    @Nullable
    public static <T> T copyNullableProperties(@Nullable Object source, Class<T> targetClazz) throws BeansException {
        if (source == null) {
            return null;
        } else {
            T to = newInstance(targetClazz);
            copyProperties((Object)source, (Object)to);
            return to;
        }
    }

    public static <T> T newInstance(Class<?> clazz) {
        return (T) instantiateClass(clazz);
    }

    public static <T, M> List<T> copyObjects(List<M> sources, Class<T> clazz) {
        if (Objects.isNull(sources) || Objects.isNull(clazz))
            throw new IllegalArgumentException();
        return Optional.of(sources)
                .orElse(new ArrayList<>())
                .stream().map(m -> copyProperties(m, clazz))
                .collect(Collectors.toList());
    }
}
