package com.atguigu.cart.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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
