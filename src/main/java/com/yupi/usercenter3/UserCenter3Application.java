package com.yupi.usercenter3;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yupi.usercenter3.mapper")
public class UserCenter3Application {

    public static void main(String[] args) {
        SpringApplication.run(UserCenter3Application.class, args);
    }

}
