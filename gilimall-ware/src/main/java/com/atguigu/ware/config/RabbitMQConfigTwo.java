package com.atguigu.ware.config;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * @Author qiang.long
 * @Date 2024/07/19
 * @Description 配置RabbitMQ消息序列化机制 （防止循环依赖，不和MyRabbitConfig写一块）
 **/
@Configuration
public class RabbitMQConfigTwo {
    /**
     * @description: 使用JSON序列化机制，进行消息转换
     */
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}
