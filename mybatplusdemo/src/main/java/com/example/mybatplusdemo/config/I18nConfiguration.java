package com.example.mybatplusdemo.config;

import com.example.mybatplusdemo.utils.I18nUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;


@Configuration
public class I18nConfiguration implements MessageSourceAware {

    @Bean
    public LocaleResolver localeResolver(){//前端传入：Accept-Language
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        Locale locale = Locale.SIMPLIFIED_CHINESE;
        localeResolver.setDefaultLocale(locale);
        LocaleContextHolder.setDefaultLocale(locale);
        return localeResolver;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        I18nUtils.setMessageSource(messageSource);
    }
}
