package com.atguigu.order.config;
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
     * @description: 设置回调
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
     * @description: 创建死信队列 order.delay.queue
     **/
    @Bean
    public Queue orderDelayQueue(){
        Map<String, Object> arguments=new HashMap<>();
        arguments.put("x-dead-letter-exchange","order-event-exchange");
        arguments.put("x-dead-letter-routing-key","order.release.order");
        arguments.put("x-message-ttl",60000); // 单位ms ，即1min
        Queue queue=new Queue("order.delay.queue", true,false,false,arguments);
        return queue;
    }

    /**
     * @description: 创建队列 order.release.order.queue
     **/
    @Bean
    public Queue orderReleaseOrderQueue(){
        Queue queue=new Queue("order.release.order.queue", true,false,false);
        return queue;
    }

    /**
     * @description: 创建交换器：order-event-exchange
     **/
    @Bean
    public Exchange orderEventExchange(){
        TopicExchange topicExchange=new TopicExchange("order-event-exchange",true,false);
        return topicExchange;
    }
    /**
     * @description: 创建绑定关系：orderCreateOrderBinding
     **/
    @Bean
    public Binding orderCreateOrderBinding(){
        Binding binding=new Binding("order.delay.queue", Binding.DestinationType.QUEUE,"order-event-exchange","order.create.order",null);
        return binding;
    }
    /**
     * @description: 创建绑定关系：orderReleaseOrderBinding
     **/
    @Bean
    public Binding orderReleaseOrderBinding(){
        Binding binding=new Binding("order.release.order.queue", Binding.DestinationType.QUEUE
                ,"order-event-exchange","order.release.order",null);
        return binding;
    }

    /**
     * @description: 订单关闭直接和库存解锁队列绑定
     **/
    @Bean
    public Binding orderReleaseOtherBinding(){
        Binding binding=new Binding(WareConstant.STOCK_RELEASE_STOCK_QUEUE, Binding.DestinationType.QUEUE
                ,"order-event-exchange","order.release.other.#",null);
        return binding;
    }
}
