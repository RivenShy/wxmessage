package com.example.mybatplusdemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.example.mybatplusdemo.mapper")
//@ComponentScan("com.example.mybatplusdemo")
public class MybatplusdemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MybatplusdemoApplication.class, args);
		System.out.println("fsdf");
	}

}
