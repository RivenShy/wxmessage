package com.example.demo.controller;

import com.example.demo.result.R;
import com.example.demo.util.WxUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wxtoken")
public class WxTokenController {

    @GetMapping("/getToken")
    public R getToken() {
//        return R.data("tests11");
        return R.data(WxUtil.getAccessToken());
    }
}
