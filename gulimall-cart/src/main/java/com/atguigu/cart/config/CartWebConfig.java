package com.atguigu.cart.config;
import com.atguigu.cart.interceptor.CartInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/**
 * @Author qiang.long
 * @Date 2024/09/10
 * @Description 页面跳转
 **/
@Configuration
public class CartWebConfig implements WebMvcConfigurer {
    /**
     * @description: 配置页面跳转
     **/
    @Override
    public void addViewControllers(ViewControllerRegistry registry){
        registry.addViewController("/cartList.html").setViewName("cartList");
        registry.addViewController("/success.html").setViewName("success");
    }

    /**
     * @description: 添加拦截器
     **/

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        // 添加CartInterceptor拦截器，并拦截当前购物车的所有请求
        registry.addInterceptor(new CartInterceptor()).addPathPatterns("/**");
    }
}