package com.example.mybatplusdemo.config;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import java.lang.reflect.Field;
import java.util.concurrent.Callable;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
//import org.springrabbit.core.secure.utils.AuthUtil;
//import org.springrabbit.core.tool.utils.ClassUtil;
//import org.springrabbit.core.tool.utils.Func;
//import org.springrabbit.core.tool.utils.ObjectUtil;
//import org.springrabbit.core.tool.utils.ReflectUtil;
//import org.springrabbit.core.tool.utils.SpringUtil;
//import org.springrabbit.core.tool.utils.StringUtil;

public class CacheUtil {
    private static CacheManager cacheManager;
    private static final Boolean TENANT_MODE;

    public CacheUtil() {
    }

    private static CacheManager getCacheManager() {
        if (cacheManager == null) {
            cacheManager = (CacheManager) SpringUtil.getBean(CacheManager.class);
        }

        return cacheManager;
    }

    public static Cache getCache(String cacheName) {
        return getCache(cacheName, TENANT_MODE);
    }

    public static Cache getCache(String cacheName, Boolean tenantMode) {
        return getCacheManager().getCache(formatCacheName(cacheName, tenantMode));
    }

    public static String formatCacheName(String cacheName, Boolean tenantMode) {
        if (!tenantMode) {
            return cacheName;
        } else {
            String tenantId = "";
            return StringUtils.hasText(tenantId) ? cacheName : tenantId.concat(":").concat(cacheName);
        }
    }

    @Nullable
    public static Object get(String cacheName, String keyPrefix, Object key) {
        return get(cacheName, keyPrefix, key, TENANT_MODE);
    }

    @Nullable
    public static Object get(String cacheName, String keyPrefix, Object key, Boolean tenantMode) {
        if (Func.hasEmpty(new Object[]{cacheName, keyPrefix, key})) {
            return null;
        } else {
            Cache.ValueWrapper valueWrapper = getCache(cacheName, tenantMode).get(keyPrefix.concat(String.valueOf(key)));
            return Func.isEmpty(valueWrapper) ? null : valueWrapper.get();
        }
    }

    @Nullable
    public static <T> T get(String cacheName, String keyPrefix, Object key, @Nullable Class<T> type) {
        return get(cacheName, keyPrefix, key, type, TENANT_MODE);
    }

    @Nullable
    public static <T> T get(String cacheName, String keyPrefix, Object key, @Nullable Class<T> type, Boolean tenantMode) {
        return Func.hasEmpty(new Object[]{cacheName, keyPrefix, key}) ? null : getCache(cacheName, tenantMode).get(keyPrefix.concat(String.valueOf(key)), type);
    }

    @Nullable
    public static <T> T get(String cacheName, String keyPrefix, Object key, Callable<T> valueLoader) {
        return get(cacheName, keyPrefix, key, valueLoader, TENANT_MODE);
    }

    @Nullable
    public static <T> T get(String cacheName, String keyPrefix, Object key, Callable<T> valueLoader, Boolean tenantMode) {
        if (Func.hasEmpty(new Object[]{cacheName, keyPrefix, key})) {
            return null;
        } else {
            try {
                Cache.ValueWrapper valueWrapper = getCache(cacheName, tenantMode).get(keyPrefix.concat(String.valueOf(key)));
                Object value = null;
                if (valueWrapper == null) {
                    T call = valueLoader.call();
                    if (ObjectUtil.isNotEmpty(call)) {
                        Field field = ReflectUtil.getField(call.getClass(), "id");
                        if (ObjectUtil.isNotEmpty(field) && ObjectUtil.isEmpty(ClassUtil.getMethod(call.getClass(), "getId", new Class[0]).invoke(call))) {
                            return null;
                        }

                        getCache(cacheName, tenantMode).put(keyPrefix.concat(String.valueOf(key)), call);
                        value = call;
                    }
                } else {
                    value = valueWrapper.get();
                    System.out.println("从缓存中获取---------" + value);
                }

                return (T) value;
            } catch (Exception var9) {
                Exception ex = var9;
                ex.printStackTrace();
                return null;
            }
        }
    }

    public static void put(String cacheName, String keyPrefix, Object key, @Nullable Object value) {
        put(cacheName, keyPrefix, key, value, TENANT_MODE);
    }

    public static void put(String cacheName, String keyPrefix, Object key, @Nullable Object value, Boolean tenantMode) {
        getCache(cacheName, tenantMode).put(keyPrefix.concat(String.valueOf(key)), value);
    }

    public static void evict(String cacheName, String keyPrefix, Object key) {
        evict(cacheName, keyPrefix, key, TENANT_MODE);
    }

    public static void evict(String cacheName, String keyPrefix, Object key, Boolean tenantMode) {
        if (!Func.hasEmpty(new Object[]{cacheName, keyPrefix, key})) {
            getCache(cacheName, tenantMode).evict(keyPrefix.concat(String.valueOf(key)));
        }
    }

    public static void clear(String cacheName) {
        clear(cacheName, TENANT_MODE);
    }

    public static void clear(String cacheName, Boolean tenantMode) {
        if (!Func.isEmpty(cacheName)) {
            getCache(cacheName, tenantMode).clear();
        }
    }

    public static void clearAllByCacheName(String cacheName) {
        clear(cacheName, true);
        clear(cacheName, false);
    }

    static {
        TENANT_MODE = Boolean.TRUE;
    }
}
