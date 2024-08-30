package com.example.mybatplusdemo.config.i18n;

import com.example.mybatplusdemo.config.Func;
import lombok.Getter;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderResolver {


    /**
     * 匹配以 '{' 开头，以 '}' 结尾的字符串
     */
    public static final Pattern pattern = Pattern.compile("^\\{(.*)\\}$");
    /**
     * 默认单例占位符解析器，即占位符前缀为"{", 后缀为"}"
     */
    @Getter
    private static final PlaceholderResolver defaultResolver = new PlaceholderResolver();

    /**
     * 根据替换规则来替换指定模板中的占位符值
     *
     * @param content 要解析的字符串
     * @param rule    解析规则回调
     */
    public String resolveByRule(String content, Function<String, String> rule) {
        Matcher matcher = pattern.matcher(content);
        if(!matcher.find()){
            return content;
        }
        String includeContent = matcher.group(1);
        String replaceContent = Func.toStr(rule.apply(includeContent), "服务器异常(Server exception)");
        return replaceContent;
    }

}