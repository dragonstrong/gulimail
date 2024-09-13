package com.atguigu.cart.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/**
 * @Author qiang.long
 * @Date 2024/09/10
 * @Description 页面跳转
 **/
@Configuration
public class CartWebConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry){
        registry.addViewController("/cartList.html").setViewName("cartList");
        registry.addViewController("/success.html").setViewName("success");
    }
}