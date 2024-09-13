package com.atguigu.authserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
/**
 * @description:  权限认证服务  - 注册登录
 **/
@EnableRedisHttpSession  // 整合redis作为session存储
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class GulimallAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallAuthServerApplication.class, args);
    }

}
