package com.atguigu.product.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
/**
 * @Author qiang.long
 * @Date 2024/09/13
 * @Description Session配置
 **/
@Configuration
public class GulimallSessionConfig {
    /**
     * 配置Session作用域
     **/
    @Bean
    public CookieSerializer cookieSerializer(){
        DefaultCookieSerializer cookieSerializer=new DefaultCookieSerializer();
        cookieSerializer.setDomainName("gulimall.com"); // 设置作用域：放大到整个项目  auth.gulimall.com -> .gulimall.com
        cookieSerializer.setCookieName("GULISESSION");
        return cookieSerializer;
    }

    /**
     * 配置Session 序列化器：默认用jdk 此处使用jackson
     **/
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer(){
        return new GenericJackson2JsonRedisSerializer();
    }
}
