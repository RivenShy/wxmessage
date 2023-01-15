package com.example.mybatplusdemo.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//也可以在引导类MybatisplusQuickstartApplication中加入@import()
@Configuration
public class MPConfig {
    @Bean
    public MybatisPlusInterceptor mpInterceptor(){
        //创建MybatisPlusInterceptor拦截器对象
        MybatisPlusInterceptor mp = new MybatisPlusInterceptor();
        //添加分页拦截器
        mp.addInnerInterceptor(new PaginationInnerInterceptor());
        //添加乐观锁拦截器
        mp.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return mp;
    }
}
