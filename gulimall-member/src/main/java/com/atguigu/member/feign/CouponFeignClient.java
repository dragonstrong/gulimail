package com.atguigu.member.feign;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
/**
 * @Author qiang.long
 * @Date 2024/03/19
 * @Description  open Feign远程调用 gulimall-coupon
 *
 * 步骤总结：
 * 1. 被调和调用服务都注册到了注册中心
 * 2. pom.xml 引入open Feign依赖 (新版本的弃用了robbin做负载均衡，还需引入spring-cloud-starter-loadbalancer依赖)
 * 3. 编写一个接口，并加上@FeignClient注解： 里面配置远程调用的服务名
 * 4. 声明接口调用远程服务的哪个接口，例如@GetMapping("/coupon/coupon/member/list") 配上接口地址
 * 5. 调用方开启远程调用功能：主程序加 @EnableFeignClients(basePackages = "com.atguigu.member.feign") 注解, basePackages填feign接口包路径
 **/


@FeignClient("gulimall-coupon")
public interface CouponFeignClient {

    @GetMapping("/coupon/coupon/member/list")
    public R member();
}
