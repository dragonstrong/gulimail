package com.atguigu.product;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;
/**
 * 1、整合mybatis-plus (参考官方文档 https://baomidou.com/)
 * (1)导入依赖 (在gulimall-common中)
 *       <dependency>
 *             <groupId>com.baomidou</groupId>
 *             <artifactId>mybatis-plus-boot-starter</artifactId>
 *             <version>3.5.5</version>
 *         </dependency>
 * （2) 配置
 *       1、配置数据源
 *           1)导入数据库驱动  (在gulimall-common中)
 *           2)application.yml中配置数据源相关信息（datasource）
 *       2、配置mybatis-plus
 *          1) @MapperScan  : 主启动类中配置Mapper接口路径（用@Mapper注解的）
 *          2） 告诉mybatius-plus, sql映射文件的位置
 *
 *
 **/
@EnableAspectJAutoProxy
@EnableTransactionManagement
@EnableFeignClients(basePackages = "com.atguigu.product.feign")
@EnableRabbit
@EnableDiscoveryClient
@MapperScan("com.atguigu.product.dao")
@SpringBootApplication
public class GulimallProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }
}
