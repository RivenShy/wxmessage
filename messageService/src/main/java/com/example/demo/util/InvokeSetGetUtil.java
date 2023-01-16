package com.example.demo.util;

import com.example.demo.entity.AuditDelayCount;

import java.lang.reflect.Method;
import java.util.Date;

public class InvokeSetGetUtil {
    public static void main(String[] args) throws NoSuchMethodException {
        Class<?> c = null;
        Object obj = null;
        try {
            c = Class.forName("com.example.demo.entity.AuditDelayCount");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            obj = c.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
//        setter(obj, "jobuser", "张泽", String.class);
//        setter(obj, "adcount", 18, int.class);

//        System.out.print("用户代码：");
//        getter(obj, "jobuser");
//        System.out.print("总审批：");
//        getter(obj, "adcount");
//        System.out.print("延期：");
//        getter(obj, "delaycount");
//        AuditDelayCount auditDelayCount = new AuditDelayCount();
//        auditDelayCount.setJobuser("10086");
//        auditDelayCount.setDelaycount(1);
//        auditDelayCount.setAdcount(2);
//        AuditDelayCount obj2 = auditDelayCount;
//        System.out.print("用户代码：");
//        getter(obj2, "jobuser");
//        System.out.print("总审批：");
//        getter(obj2, "adcount");
//        System.out.print("延期：");
//        getter(obj2, "delaycount");

        AuditDelayCount auditDelayCount = new AuditDelayCount();
//		Object objValue = InvokeSetGetUtil.getter(auditDelayCount, "dateTest");
        Method met = auditDelayCount.getClass().getMethod("get" + InvokeSetGetUtil.initStr("dateTest"));
        System.out.println(met);
        System.out.println(met.getDeclaringClass());
        System.out.println(met.getReturnType());
        System.out.println(met.getReturnType().equals(Date.class));
        met = auditDelayCount.getClass().getMethod("get" + InvokeSetGetUtil.initStr("adcount"));
        System.out.println(met.getReturnType());
        System.out.println(met.getReturnType().equals(int.class));
        met = auditDelayCount.getClass().getMethod("get" + InvokeSetGetUtil.initStr("jobuser"));
        System.out.println(met.getReturnType());
        System.out.println(met.getReturnType().equals(String.class));
    }

    /*
     *@param obj 操作的对象
     *@param att 操作的属性
     *@param value 设置的值
     *@param type 参数的类型
     */
    public static void setter(Object obj, String att, Object value, Class<?> type) {
        try {
            Method met = obj.getClass().
                    getMethod("set" + initStr(att), type);
            met.invoke(obj, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static void getter(Object obj, String att) {
//        try {
//            Method met = obj.getClass().getMethod("get" + initStr(att));
//            System.out.println(met.invoke(obj));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static Object getter(Object obj, String att) {
        try {
            Method met = obj.getClass().getMethod("get" + initStr(att));
//            System.out.println(met.invoke(obj));
            return met.invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Class getMethodReturnType(Object obj, String att) {
        try {
            Method met = obj.getClass().getMethod("get" + initStr(att));
            return met.getReturnType();
        } catch (Exception e) {
            return null;
        }
    }


    public static String initStr(String old) {   // 将单词的首字母大写
        String str = old.substring(0, 1).toUpperCase() + old.substring(1);
        return str;
    }
}
