package com.atguigu.ware;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@MapperScan("com.atguigu.ware.dao")
@SpringBootApplication
public class GilimallWareApplication {
    public static void main(String[] args) {
        SpringApplication.run(GilimallWareApplication.class, args);
    }
}
