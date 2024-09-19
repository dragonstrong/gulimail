package com.atguigu.order.web;
import com.atguigu.order.entity.OrderEntity;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.UUID;
/**
 * @Author qiang.long
 * @Date 2024/09/14
 * @Description
 **/
@Controller
public class HelloController {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @ResponseBody
    @GetMapping("/test/createOrder")
    public String createOrder(){
        OrderEntity order=new OrderEntity();
        order.setOrderSn(UUID.randomUUID().toString());
        order.setCreateTime(new Date());
        rabbitTemplate.convertAndSend("order-event-exchange","order.create.order",order); // 给MQ发送消息
        return "ok";
    }
    @GetMapping("/{page}.html")
    public String hello(@PathVariable("page") String page){
        return page;
    }
}
