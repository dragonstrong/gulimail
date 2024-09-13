package com.atguigu.gulimall.gulimallthirdparty.config;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.teaopenapi.models.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * @Author qiang.long
 * @Date 2024/09/11
 * @Description 短信验证码配置
 **/
@Configuration
public class SmsConfig {
    @Value("${alibaba.oss.accessKeyId}")
    private String accessKeyId;
    @Value("${alibaba.oss.accessKeySecret}")
    private String accessKeySecret;
    @Bean
    public Client createClient() throws Exception {
        Config config = new Config()
                // 配置 AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 配置 AccessKey Secret
                .setAccessKeySecret(accessKeySecret);

        // 配置 Endpoint
        config.endpoint = "dysmsapi.aliyuncs.com";

        return new Client(config);
    }
}
