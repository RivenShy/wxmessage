package com.example.mybatplusdemo.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.example.mybatplusdemo.config.Func;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CollectionUtilsPan {

    private CollectionUtilsPan() {}

    /**
     * 使用属性过滤集合对象重复数据
     * @param keyExtractor 去重属性
     * @param <T> 泛型
     * @return Predicate
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public static <T> ArrayList<T> newArrayList(T... values) {
        Assert.notEmpty(values, "array is null");
        ArrayList<T> list = new ArrayList<>(values.length * 2);
        Collections.addAll(list, values);
        return list;
    }

    public static <T> Set<T> newHashSet(T... values) {
        Assert.notEmpty(values, "array is null");
        Set<T> set = new HashSet<>(values.length * 2);
        Collections.addAll(set, values);
        return set;
    }

    public static  <T, K, V> Map<K, V> toMap(List<T> params,
                                             Function<T, K> k,
                                             Function<T, V> v) {
        if (CollectionUtil.isEmpty(params)) {
            return new HashMap<>();
        }
        return 	toMap(params, Objects::nonNull, k, v, (k1, k2) -> k2);
    }

    public static  <T, K, V> Map<K, V> toMap(List<T> params,
                                             Function<T, K> k,
                                             Function<T, V> v,
                                             BinaryOperator<V> mergeFunction) {
        if (CollectionUtil.isEmpty(params)) {
            return new HashMap<>();
        }
        return 	toMap(params, Objects::nonNull, k, v, mergeFunction);
    }

    public static  <T, K, V> Map<K, V> toMap(List<T> params,
                                             Predicate<T> predicate,
                                             Function<T, K> k,
                                             Function<T, V> v,
                                             BinaryOperator<V> mergeFunction) {
        if (CollectionUtil.isEmpty(params)) {
            return new HashMap<>();
        }
        return 	params.stream().filter(predicate).collect(Collectors.toMap(k, v, mergeFunction));
    }

    public static <T, R> List<R> convert(List<T> params, List<T> d, Function<T, R> function) {
        if (CollectionUtil.isEmpty(params) && CollectionUtil.isEmpty(d)) {
            return new ArrayList<>();
        }
        return convert(CollectionUtil.isEmpty(params) ? d : params, function);
    }

    public static <T, R> List<R> convert(List<T> params,
                                         Function<T, R> function) {
        if (CollectionUtil.isEmpty(params)) {
            return new ArrayList<>();
        }
        return convert(params, Objects::nonNull, function);
    }

    public static <T, R> List<R> convert(List<T> params,
                                         Predicate<R> predicate,
                                         Function<T, R> function) {
        if (CollectionUtil.isEmpty(params)) {
            return new ArrayList<>();
        }
        return params.stream().map(function).filter(predicate).distinct().collect(Collectors.toList());
    }

    public static <T> List<String> splicingLimit(List<T> params, int size,
                                                 Function<T, String> function) {
        if (CollectionUtil.isEmpty(params)) {
            return new ArrayList<>();
        }
        int pageNum = 0;
        List<String> result = new ArrayList<>();
        do {
            int skipNum = pageNum++ * size;
            String str = params.stream().map(function).filter(StringUtil::isNotBlank).skip(skipNum).limit(size)
                    .collect(Collectors.joining(","));
            if (Func.isEmpty(str)) {
                break;
            }
            result.add(str);
        } while (true);
        return result;
    }
}
