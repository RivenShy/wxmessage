package com.example.mybatplusdemo;

import okhttp3.OkHttpClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@MapperScan("com.example.mybatplusdemo.mapper")
//@ComponentScan("com.example.mybatplusdemo")
@EnableCaching
public class MybatplusdemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MybatplusdemoApplication.class, args);
		System.out.println("springboot started");
	}

	@Bean
	public OkHttpClient okHttpClient() {
		return new OkHttpClient.Builder()
				.readTimeout(300, TimeUnit.SECONDS)
				.connectTimeout(300, TimeUnit.SECONDS)
				.writeTimeout(300, TimeUnit.SECONDS)
				.build();
	}
}
