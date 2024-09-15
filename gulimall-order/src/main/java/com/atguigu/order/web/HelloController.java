package com.atguigu.order.web;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
/**
 * @Author qiang.long
 * @Date 2024/09/14
 * @Description
 **/
@Controller
public class HelloController {
    @GetMapping("/{page}.html")
    public String hello(@PathVariable("page") String page){
        return page;
    }
}
