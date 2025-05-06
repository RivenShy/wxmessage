package com.example.mybatplusdemo.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.Map;

public class Condition {
    public Condition() {
    }

    public static <T> IPage<T> getPage(Query query) {
        Page<T> page = new Page((long)Func.toInt(query.getCurrent(), 1), (long)Func.toInt(query.getSize(), 10));
//        page.setAsc(Func.toStrArray(SqlKeyword.filter(query.getAscs())));
//        page.setDesc(Func.toStrArray(SqlKeyword.filter(query.getDescs())));
        return page;
    }

    public static <T> IPage<T> getPage(Query query, Boolean isCount) {
        Page<T> page = new Page((long)Func.toInt(query.getCurrent(), 1), (long)Func.toInt(query.getSize(), 10));
        page.setSearchCount(Func.toBoolean(isCount, Boolean.TRUE));
//        page.setAsc(Func.toStrArray(SqlKeyword.filter(query.getAscs())));
//        page.setDesc(Func.toStrArray(SqlKeyword.filter(query.getDescs())));
        return page;
    }

    public static <T> QueryWrapper<T> getQueryWrapper(T entity) {
        return new QueryWrapper(entity);
    }

    public static <T> QueryWrapper<T> getQueryWrapper(Map<String, Object> query, Class<T> clazz) {
        Kv exclude = Kv.create().set("Rabbit-Auth", "Rabbit-Auth").set("current", "current").set("size", "size").set("ascs", "ascs").set("descs", "descs");
        return getQueryWrapper(query, exclude, clazz);
    }

    public static <T> QueryWrapper<T> getQueryWrapper(Map<String, Object> query, Map<String, Object> exclude, Class<T> clazz) {
        exclude.forEach((k, v) -> {
            query.remove(k);
        });
        QueryWrapper<T> qw = new QueryWrapper();
//        qw.setEntity(BeanUtil.newInstance(clazz));
//        SqlKeyword.buildCondition(query, qw);
        return qw;
    }
}
