package com.example.mybatplusdemo.config.i18n;

import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.example.mybatplusdemo.utils.I18nUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * excel导出国际化
 */

@RequiredArgsConstructor
public class I18nCellWriteHandler implements CellWriteHandler {


    @Override
    public void beforeCellCreate(CellWriteHandlerContext context) {
        // 先注释
//        if (!context.getHead()) {
//            return;
//        }
//        final List<String> originHeadNames = context.getHeadData().getHeadNameList();
//        if (CollectionUtils.isEmpty(originHeadNames)) {
//            return;
//        }
//        List<String> newHeadNames = originHeadNames.stream().
//                map(headName -> PlaceholderResolver.getDefaultResolver()
//                        .resolveByRule(headName, this::getMessage)).
//                collect(Collectors.toList());
//        context.getHeadData().setHeadNameList(newHeadNames);
    }

    public String getMessage(String code) {
        Locale locale = LocaleContextHolder.getLocale();
        return I18nUtils.getMessage(code, "服务器异常(Server exception)", locale);
    }
}