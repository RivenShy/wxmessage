package com.example.demo.config;

import com.example.demo.controller.CustomerController;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//自定义mvc配置类
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    //用来全局处理跨域
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")   //处理的请求地址
//                .allowedMethods("*")
//                .allowedOrigins("*")
//                .allowedHeaders("*")
//                .allowCredentials(false)
//                .exposedHeaders("")
//                .maxAge(3600);
//    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        log.info("开始进行静态资源映射。。。");

//        本地
        registry.addResourceHandler(CustomerController.logoLogicPath + "**")
                .addResourceLocations("file:" + CustomerController.logoRealPath);

//        服务器
//        registry.addResourceHandler(CustomerController.logoPath + "**")
//                .addResourceLocations("file:/home/customerLogo/");
//
//        registry.addResourceHandler("/backend/**")
//                .addResourceLocations("classpath:/img/");
//        registry.addResourceHandler("/front/**")
//                .addResourceLocations("classpath:/static/img/");
    }

}
