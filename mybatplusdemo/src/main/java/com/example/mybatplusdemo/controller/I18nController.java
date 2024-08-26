package com.example.mybatplusdemo.controller;

import com.example.mybatplusdemo.utils.I18nUtils;
import com.example.mybatplusdemo.utils.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/i18n")
public class I18nController {


    @GetMapping("/hello")
    public R<String> hello() {
        return R.success(I18nUtils.getMessage("hello"));
    }
}
