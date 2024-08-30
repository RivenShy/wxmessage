package com.example.mybatplusdemo.utils;

import org.springframework.util.StringUtils;

public class StringUtil extends StringUtils {

    public static boolean isNotBlank(final CharSequence cs) {
        return hasText(cs);
    }

}
