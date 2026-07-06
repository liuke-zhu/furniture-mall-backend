package com.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@MapperScan("com.mall.mapper")
@EnableAsync
@SpringBootApplication
public class FurnitureMallApplication {

    public static void main(String[] args) {
        SpringApplication.run(FurnitureMallApplication.class, args);
    }
}
