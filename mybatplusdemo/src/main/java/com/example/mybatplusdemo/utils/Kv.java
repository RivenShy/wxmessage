package com.example.mybatplusdemo.utils;

import org.springframework.util.LinkedCaseInsensitiveMap;

import java.util.HashMap;
import java.util.Map;

public class Kv extends LinkedCaseInsensitiveMap<Object> {
    private Kv() {
    }

    public static Kv create() {
        return new Kv();
    }

    public static HashMap newMap() {
        return new HashMap(16);
    }

    public Kv set(String attr, Object value) {
        this.put(attr, value);
        return this;
    }

    public Kv setAll(Map map) {
        this.putAll(map);
        return this;
    }

    public Kv setIgnoreNull(String attr, Object value) {
        if (null != attr && null != value) {
            this.set(attr, value);
        }

        return this;
    }

    public Object getObj(String key) {
        return super.get(key);
    }

//    public <T> T get(String attr, T defaultValue) {
//        Object result = this.get(attr);
//        return result != null ? result : defaultValue;
//    }

    public String getStr(String attr) {
        return Func.toStr(this.get(attr), (String)null);
    }

    public Integer getInt(String attr) {
        return Func.toInt(this.get(attr), -1);
    }

    public Long getLong(String attr) {
        return Func.toLong(this.get(attr), -1L);
    }

    public Float getFloat(String attr) {
        return Func.toFloat(this.get(attr), (Float)null);
    }

    public Double getDouble(String attr) {
        return Func.toDouble(this.get(attr), (Double)null);
    }

    public Boolean getBool(String attr) {
        return Func.toBoolean(this.get(attr), (Boolean)null);
    }

//    public byte[] getBytes(String attr) {
//        return (byte[])this.get(attr, (Object)null);
//    }
//
//    public Date getDate(String attr) {
//        return (Date)this.get(attr, (Object)null);
//    }
//
//    public Time getTime(String attr) {
//        return (Time)this.get(attr, (Object)null);
//    }
//
//    public Timestamp getTimestamp(String attr) {
//        return (Timestamp)this.get(attr, (Object)null);
//    }
//
//    public Number getNumber(String attr) {
//        return (Number)this.get(attr, (Object)null);
//    }

    public Kv clone() {
        return (Kv)super.clone();
    }
}
