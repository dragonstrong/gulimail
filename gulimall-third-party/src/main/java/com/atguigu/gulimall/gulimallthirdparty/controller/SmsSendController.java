package com.atguigu.gulimall.gulimallthirdparty.controller;
import com.atguigu.common.utils.Result;
import com.atguigu.gulimall.gulimallthirdparty.component.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/**
 * @Author qiang.long
 * @Date 2024/09/11
 * @Description 验证码发送
 **/
@RestController
public class SmsSendController {
    @Autowired
    SmsComponent smsComponent;
    /**
     * @description: 发送验证码
     * @param:
     * @param phone 手机号
     * @param code 验证码
     * @return: com.atguigu.common.utils.Result
     **/
    @GetMapping("/sms/sendcode")
    public Result sendCode(@RequestParam("phone") String phone,@RequestParam("code") String code) throws Exception {
        smsComponent.sendSms(phone,code);
        return Result.ok();
    }
}
