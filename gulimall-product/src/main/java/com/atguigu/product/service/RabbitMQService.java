package com.atguigu.product.service;
import com.atguigu.product.dto.rabbit.ExchangeDTO;
import com.atguigu.product.dto.rabbit.MessageDTO;
/**
 * @Author qiang.long
 * @Date 2024/07/18
 * @Description RabbitMQ测试
 **/
public interface RabbitMQService {

    /**
     * @description:创建交换机
     */
    public void createExchange(ExchangeDTO exchangeDTO);
    /**
     * @description:创建队列
     */
    public void createQueue();
    /**
     * @description:创建绑定
     */
    public void createBinding();
    /**
     * @description:发送消息
     */
    public void sendMessage(MessageDTO messageDTO);
    public void sendMessage2(int num);


}
