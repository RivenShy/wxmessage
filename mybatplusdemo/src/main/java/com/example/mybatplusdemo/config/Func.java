package com.example.mybatplusdemo.config;

import com.example.mybatplusdemo.utils.StringUtil;
import org.springframework.lang.Nullable;

import java.util.Objects;

public class Func {

    public static boolean hasEmpty(Object... os) {
        Object[] var1 = os;
        int var2 = os.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            Object o = var1[var3];
            if (isEmpty(o)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isEmpty(@Nullable Object obj) {
        return ObjectUtil.isEmpty(obj);
    }

    public static String toStr(Object str, String defaultValue) {
        return null != str && !str.equals("null") ? String.valueOf(str) : defaultValue;
    }

    public static boolean isNull(@Nullable Object obj) {
        return Objects.isNull(obj);
    }

    public static boolean isNotBlank(@Nullable final CharSequence cs) {
        return StringUtil.isNotBlank(cs);
    }
}
