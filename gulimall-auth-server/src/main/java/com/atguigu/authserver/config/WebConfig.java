package com.atguigu.authserver.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/**
 * @Author qiang.long
 * @Date 2024/09/10
 * @Description 页面跳转配置
 **/
@Configuration
public class WebConfig implements WebMvcConfigurer {
    /**
     * 页面跳转，等价于写Controller
     *     @GetMapping("/login.html")
     *     public String loginPage(){
     *         return "login";
     *     }
     *
     *     @GetMapping("/register.html")
     *     public String registerPage(){
     *         return "register";
     *     }
     *
     *
     **/

    @Override
    public void addViewControllers(ViewControllerRegistry registry){
        //registry.addViewController("/login.html").setViewName("login");
        registry.addViewController("/register.html").setViewName("register");
    }
}
