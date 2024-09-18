package com.atguigu.order;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableAspectJAutoProxy(exposeProxy = true)
@EnableFeignClients
@EnableRedisHttpSession
@EnableDiscoveryClient
@MapperScan("com.atguigu.order.dao")
@SpringBootApplication
public class GulimallOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(GulimallOrderApplication.class, args);
    }
}
