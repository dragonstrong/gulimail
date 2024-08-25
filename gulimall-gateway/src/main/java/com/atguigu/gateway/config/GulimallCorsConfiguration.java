package com.atguigu.gateway.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
/**
 * @Author qiang.long
 * @Date 2024/08/25
 * @Description 配置允许跨域
 **/
@Configuration
public class GulimallCorsConfiguration {
    @Bean
    public CorsWebFilter corsWebFilter(){
        CorsConfiguration corsConfiguration=new CorsConfiguration();
        /**
         * 1.配置跨域
         **/
        corsConfiguration.addAllowedHeader("*"); // 任意请求头都可以跨域
        corsConfiguration.addAllowedMethod("*"); // 任意请求方式都可以跨域（GET POST等）
        corsConfiguration.addAllowedOriginPattern("*"); // 任意请求来源都可以跨域
        corsConfiguration.setAllowCredentials(true); // 允许cookie

        UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",corsConfiguration); // 所有uri都能跨
        return new CorsWebFilter(source);
    }
}
