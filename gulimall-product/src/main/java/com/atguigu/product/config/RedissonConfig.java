package com.atguigu.product.config;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
/**
 * @Author qiang.long
 * @Date 2024/03/27
 * @Description Redisson配置
 **/

@Configuration
public class RedissonConfig {
    /**
     * @description: 往容器中注入RedissonClient对象（所有redisson的操作都要通过它）
     * @param:
     * @return: org.redisson.api.RedissonClient
     **/
    @Bean(destroyMethod="shutdown")
    public RedissonClient redisson() throws IOException {
        Config config = new Config();
        config.useSingleServer()  // 单节点模式
                .setAddress("redis://192.168.30.128:6379"). // redis地址和端口
                setPassword("181181@Lq");   // 密码
        // 创建实例
        return Redisson.create(config);
    }
}
