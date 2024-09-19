package com.atguigu.order.listener;
import com.alibaba.fastjson.JSON;
import com.atguigu.order.entity.OrderEntity;
import com.atguigu.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
/**
 * @Author qiang.long
 * @Date 2024/09/18
 * @Description 定时关单
 **/
@Slf4j
@Service
@RabbitListener(queues = "order.release.order.queue") // 监听"order.release.order.queue"队列
public class OrderCloseListener {
    @Autowired
    OrderService orderService;
    @RabbitHandler
    public void handleOrderClose(OrderEntity order, Message message, Channel channel) throws IOException {
        log.info("收到定时关单消息:{}", JSON.toJSONString(order));
        try {
            orderService.closeOrder(order);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false); // 关单成功，从队列移除消息
        }catch (Exception e){ // 关单出现异常，消息重新投放到消息队列
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);

        }

    }
}
