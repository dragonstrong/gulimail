package com.atguigu.ware.config;
import com.alibaba.fastjson.JSON;
import com.atguigu.common.constant.WareConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
/**
 * @Author qiang.long
 * @Date 2024/07/18
 * @Description 配置RabbitMQ callBack回调,  @Beans创建对应的队列、交换器和绑定关系
 **/
@Slf4j
@Configuration
public class RabbitMQOneConfig {
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



    /**
     * @description: 创建死信队列 stock.delay.queue
     **/
    @Bean
    public Queue stockDelayQueue(){
        Map<String, Object> arguments=new HashMap<>();
        arguments.put("x-dead-letter-exchange","stock-event-exchange");
        arguments.put("x-dead-letter-routing-key","stock.release");
        arguments.put("x-message-ttl",120000); // 2min
        Queue queue=new Queue(WareConstant.STOCK_DELAY_QUEUE, true,false,false,arguments);
        return queue;
    }

    /**
     * @description: 创建队列 stock.release.stock.queue
     **/
    @Bean
    public Queue stockReleaseStockQueue(){
        Queue queue=new Queue(WareConstant.STOCK_RELEASE_STOCK_QUEUE, true,false,false);
        return queue;
    }

    /**
     * @description: 创建交换器：stock-event-exchange
     **/
    @Bean
    public Exchange stockEventExchange(){
        TopicExchange topicExchange=new TopicExchange(WareConstant.STOCK_EVENT_EXCHANGE,true,false);
        return topicExchange;
    }
    /**
     * @description: 创建绑定关系：stockLockedBinding
     **/
    @Bean
    public Binding stockLockedBinding(){
        Binding binding=new Binding(WareConstant.STOCK_DELAY_QUEUE, Binding.DestinationType.QUEUE,WareConstant.STOCK_EVENT_EXCHANGE,WareConstant.STOCK_LOCKED_ROUTING_KEY,null);
        return binding;
    }
    /**
     * @description: 创建绑定关系：stockReleaseBinding
     **/
    @Bean
    public Binding stockReleaseBinding(){
        Binding binding=new Binding(WareConstant.STOCK_RELEASE_STOCK_QUEUE, Binding.DestinationType.QUEUE
                ,WareConstant.STOCK_EVENT_EXCHANGE,WareConstant.STOCK_RELEASE_ROUTING_KEY,null);
        return binding;
    }
}
