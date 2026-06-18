package com.sailing;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.sailing.mapper")
public class SailingApplication {
    public static void main(String[] args) {
        SpringApplication.run(SailingApplication.class, args);
    }
}
