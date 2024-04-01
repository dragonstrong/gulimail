package com.atguigu.product.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;
/**
 * @Author qiang.long
 * @Date 2024/04/01
 * @Description 线程池参数配置
 **/
@Configuration
public class ThreadConfig {
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties pool){
        return new ThreadPoolExecutor(pool.getCorePoolSize(), pool.getMaximumPoolSize(),
                pool.getKeepAliveTime(),TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(10000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }
}
