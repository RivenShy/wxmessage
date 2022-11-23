package com.example.demo.config;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sys")
public class SysLoginController {

    @PostMapping("/login")
    public String login() {
        System.out.println("路过");
        return "login";
    }

}
