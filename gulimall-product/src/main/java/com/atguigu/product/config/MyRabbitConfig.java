package com.atguigu.product.config;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @Author qiang.long
 * @Date 2024/07/18
 * @Description 配置RabbitMQ callBack回调
 **/
@Slf4j
@Configuration
public class MyRabbitConfig {
    @Autowired
    RabbitTemplate rabbitTemplate;
    /**
     * @description: 定制化RabbitTemplate
     */
    @PostConstruct // 让MyRabbitConfig对象创造完成后再调用initRabbitTemplate方法
    public void initRabbitTemplate(){
        // 设置确认回调
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback(){
            /**
             * @param correlationData :消息的唯一标识
             * @param b 消息是否成功抵达Broker
             * @param s 失败的原因
             **/
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {
                log.info("confirm...correlationData:【{}】, ack:【{}】,s:【{}】", JSON.toJSONString(correlationData),b,s);
            }
        });

        // 设置消息抵达队列回调
        rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback(){
            /**
             * @description: 只要消息未投递到指定队列，就触发回调
             ReturnedMessage里的属性：
             Message message：投递失败的消息详细信息
             int replyCode：回复的状态码
             String replyText：回复的内容
             String exchange：当时消息是发给哪个交换机的
             String routingKey：消息指定的路由键
             **/
            @Override
            public void returnedMessage(ReturnedMessage returnedMessage) {
                log.info("Fail Message:【{}】,replyCode:【{}】,replyText:【{}】,exchange:【{}】,routingKey:【{}】",
                        returnedMessage.getMessage(),
                        returnedMessage.getReplyCode(),
                        returnedMessage.getReplyText(),
                        returnedMessage.getExchange(),
                        returnedMessage.getRoutingKey());
            }
        });
    }
}
