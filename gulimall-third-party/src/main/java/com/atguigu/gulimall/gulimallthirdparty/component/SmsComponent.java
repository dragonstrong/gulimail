package com.atguigu.gulimall.gulimallthirdparty.component;
import com.alibaba.fastjson.JSON;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
/**
 * @Author qiang.long
 * @Date 2024/09/11
 * @Description 短信发送组件
 **/
@Slf4j
@Component
public class SmsComponent {
    @Value("${alibaba.sms.signName}")
    String signName;
    @Value("${alibaba.sms.templateCode}")
    String templateCode;
    @Autowired
    Client smsClient;
    /**
     * @description: 发送短信
     * @param:
     * @param phone 手机号
     * @param code 验证码
     * @return: void
     **/
    public void sendSms(String phone,String code) throws Exception {
        /* 6位随机验证码
        String code= UUID.randomUUID().toString().substring(0,5);
        //Random random=new Random();
        //int r=random.nextInt(899999);
        //int randNum=r+100000;
         */
        String templateParam="{\"code\":\""+code+"\"}";
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setPhoneNumbers(phone)
                .setSignName(signName)
                .setTemplateCode(templateCode)
                .setTemplateParam(templateParam);


        // 获取响应对象
        SendSmsResponse sendSmsResponse = smsClient.sendSms(sendSmsRequest);

        // 响应包含服务端响应的 body 和 headers
        log.info("给手机号:【{}】 发送短信验证码:{}",phone,JSON.toJSONString(sendSmsResponse));
    }
}
