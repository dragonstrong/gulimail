package com.atguigu.order.config;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
/**
 * @Author qiang.long
 * @Date 2024/09/15
 * @Description  增加拦截器：给新请求同步老请求的cookie，解决Feign远程调用丢失请求头问题
 **/
@Slf4j
@Configuration
public class FeignConfig {
    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor(){

        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                //log.info("requestInterceptor线程...{}",Thread.currentThread().getId());
                // 拿到原请求的所有属性（里面有cookie）
                ServletRequestAttributes attributes= (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
                if(attributes!=null){
                    HttpServletRequest request=attributes.getRequest(); // 老请求
                    if(request!=null){
                        // 同步请求头数据（手动设置cookie）
                        String cookie=request.getHeader("Cookie");
                        // 给新请求同步老请求的cookie
                        requestTemplate.header("Cookie",cookie);
                    }
                }
            }
        };
    }
}
