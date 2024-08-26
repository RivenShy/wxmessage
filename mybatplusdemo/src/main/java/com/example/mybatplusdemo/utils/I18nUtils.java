package com.example.mybatplusdemo.utils;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

public class I18nUtils {

    final static String I18N_SERVER_EXCEPTION = "服务器异常(Server exception)";

    private static MessageSource messageSource;

    public static void setMessageSource(MessageSource messageSource) {
        I18nUtils.messageSource = messageSource;
    }


    public static String getMessage(String code) {
        return getMessage(code, I18N_SERVER_EXCEPTION);
    }

    public static String getMessage(String code, String defMsg, Object... args) {
        return messageSource.getMessage(code, args, defMsg, LocaleContextHolder.getLocale());
    }

    public static String getCurrentLang() {
        Locale locale = LocaleContextHolder.getLocale();
        return locale.toLanguageTag().toLowerCase();
    }
}
