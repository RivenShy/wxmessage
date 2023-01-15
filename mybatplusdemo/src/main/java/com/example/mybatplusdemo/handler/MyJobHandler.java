package com.example.mybatplusdemo.handler;

import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

@Component
public class MyJobHandler {

    @XxlJob("mybatisplusTest")
    public void test() {
        System.out.println("mybatisplus & xxl-job");
    }

}
