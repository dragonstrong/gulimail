package com.atguigu.authserver.feign;
import com.atguigu.common.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
/**
 * @Author qiang.long
 * @Date 2024/09/11
 * @Description 第三方服务远程调用
 **/
@FeignClient("gulimall-third-party")
public interface ThirdPartyFeignService {
    @GetMapping("/third-party/sms/sendcode")
    Result sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code) throws Exception;

}
