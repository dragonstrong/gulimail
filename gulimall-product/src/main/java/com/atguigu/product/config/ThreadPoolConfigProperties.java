package com.atguigu.product.config;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
/**
 * @Author qiang.long
 * @Date 2024/04/01
 * @Description  线程池参数配置类
 * @ConfigurationProperties 和配置文件绑定(prefix指定前缀)
 **/

@ConfigurationProperties(prefix = "gulimall.thread")
@Component
@Data
public class ThreadPoolConfigProperties {
    /**
     * 核心线程数
     **/
    private Integer corePoolSize;
    /**
     * 最大线程数
     **/
    private Integer maximumPoolSize;
    /**
     * 休眠时长 ，单位s
     **/
    private Integer keepAliveTime;

}
