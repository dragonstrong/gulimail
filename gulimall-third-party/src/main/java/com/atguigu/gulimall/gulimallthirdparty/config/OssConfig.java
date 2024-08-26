package com.atguigu.gulimall.gulimallthirdparty.config;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * @Author qiang.long
 * @Date 2024/08/27
 * @Description 阿里云OSS配置
 **/
@Configuration
public class OssConfig {
    @Value("${alibaba.oss.endpoint}")
    private String endpoint;
    @Value("${alibaba.oss.accessKeyId}")
    private String accessKeyId;
    @Value("${alibaba.oss.accessKeySecret}")
    private String accessKeySecret;
    @Bean
    public OSS ossClient(){
        return new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
    }
}