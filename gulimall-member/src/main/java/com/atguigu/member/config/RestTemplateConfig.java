package com.atguigu.member.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
/**
 * @Author qiang.long
 * @Date 2024/04/16
 * @Description
 **/

@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
}
