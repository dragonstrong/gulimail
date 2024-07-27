package com.atguigu.product.service.impl;
import com.alibaba.fastjson.JSON;
import com.atguigu.product.dto.rabbit.ExchangeDTO;
import com.atguigu.product.dto.rabbit.MessageDTO;
import com.atguigu.product.entity.BrandEntity;
import com.atguigu.product.entity.CategoryEntity;
import com.atguigu.product.service.RabbitMQService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;
/**
 * @Author qiang.long
 * @Date 2024/07/18
 * @Description RabbitMQ测试
 **/
@Slf4j
@Service
@RabbitListener(queues = {"hello-java-queue"})
public class RabbitMQServiceImpl implements RabbitMQService {
    @Autowired
    AmqpAdmin amqpAdmin;
    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * @description:创建交换机
     */
    @Override
    public void createExchange(ExchangeDTO exchangeDTO){
        /**DirectExchange全参构造
         DirectExchange(String name【交换机名字】,
         boolean durable【是否持久化】,
         boolean autoDelete【是否自动删除】,
         Map<String, Object> arguments【其他参数，对应前端arguments】)
         **/
        Exchange exchange=null;
        switch (exchangeDTO.getType()){
            case 1: {
                exchange=new DirectExchange(exchangeDTO.getName(), exchangeDTO.isDurable(), exchangeDTO.isAutoDelete());
                break;
            }
            case 2: {
                exchange=new FanoutExchange(exchangeDTO.getName(), exchangeDTO.isDurable(), exchangeDTO.isAutoDelete());
                break;
            }
            case 3: {
                exchange=new TopicExchange(exchangeDTO.getName(), exchangeDTO.isDurable(), exchangeDTO.isAutoDelete());
                break;
            }
            default:break;
        }
        //DirectExchange directExchange=new DirectExchange("hello-java-exchange",true,false);
        amqpAdmin.declareExchange(exchange);
        log.info("Exchanges 创建成功");
    }

    /**
     * @description:创建队列
     */
    @Override
    public void createQueue(){
        /**Queue全参构造
         Queue(String name【队列名】,
         boolean durable【是否持久化】,
         boolean exclusive【是否排它，即只能让当前连接连接到该队列，一般设为false】,
         boolean autoDelete【是否自动删除】,
         @Nullable Map<String, Object> arguments【其他参数，对应前端arguments】)
         **/
        amqpAdmin.declareQueue(new Queue("hello-java-queue", true,false,false));
        log.info("Queue【{}】 创建成功");
    }


    /**
     * @description:创建绑定
     */
    @Override
    public void createBinding(){
        /**Binding全参构造
         Binding(String destination【目的地，传队列或交换机的名字】,
         DestinationType destinationType【类型，和队列还是交换机绑定】,
         String exchange【起始交换机】,
         String routingKey【路由键】,
         @Nullable Map<String, Object> arguments【自定义参数】)
         **/
        amqpAdmin.declareBinding(new Binding("hello-java-queue", Binding.DestinationType.QUEUE,"hello-java-exchange","hello-java",null));
        log.info("Binding 创建成功");
    }


    /**
     * @description:发送消息
     */
    @Override
    public void sendMessage(MessageDTO messageDTO){
        /**convertAndSend参数
         convertAndSend(String exchange【给哪个交换机发消息】,
         String routingKey【路由键】,
         Object object【消息内容】)
         **/
        //rabbitTemplate.convertAndSend("hello-java-exchange","hello-java","hello-world!");
        // 以json形式发送消息对象
        for(int i=0;i<messageDTO.getCount();i++){
            messageDTO.getCategoryEntity().setCatId(messageDTO.getCategoryEntity().getCatId()+1);
            rabbitTemplate.convertAndSend(messageDTO.getExchange(),messageDTO.getRoutingKey(),messageDTO.getCategoryEntity());
        }
        /*
        CategoryEntity category=new CategoryEntity();
        category.setCatId(1L);
        category.setName("图书");
        category.setProductUnit("本");
        rabbitTemplate.convertAndSend("hello-java-exchange","hello-java",category);
         */
        log.info("Message发送成功");
    }


    @Override
    public void sendMessage2(int num){
        /**convertAndSend参数
         convertAndSend(String exchange【给哪个交换机发消息】,
         String routingKey【路由键】,
         Object object【消息内容】)
         **/
        //rabbitTemplate.convertAndSend("hello-java-exchange","hello-java","hello-world!");
        // 以json形式发送消息对象
        for(int i=1;i<=num;i++){
            if(true){ // 偶数发CategoryEntity型消息
                CategoryEntity category=new CategoryEntity();
                category.setCatId((long)i);
                category.setName("图书");
                category.setProductUnit("本");
                rabbitTemplate.convertAndSend("hello-java-exchange","hello-java",category,new CorrelationData(UUID.randomUUID().toString())); // 最后一个参数指定消息的唯一ID
            }else{  // 奇数发BrandEntity型消息
                BrandEntity brandEntity=new BrandEntity();
                brandEntity.setBrandId((long)i);
                brandEntity.setName("华为");
                brandEntity.setLogo("HUAWEI");
                rabbitTemplate.convertAndSend("hello-java-exchange","hello-java22",brandEntity,new CorrelationData(UUID.randomUUID().toString()));
            }
        }
        /*
        CategoryEntity category=new CategoryEntity();
        category.setCatId(1L);
        category.setName("图书");
        category.setProductUnit("本");
        rabbitTemplate.convertAndSend("hello-java-exchange","hello-java",category);
         */
        log.info("Message发送成功");
    }



    /**
     * @description: 监听rabbit队列里的消息(用原生Message接)
     * @RabbitListener(queues = {"hello-java-queue"}) :queues是一个数组（可以监听多个队列），写要监听队列的名字
     * @return: void
     **/
    //@RabbitListener(queues = {"hello-java-queue"})
    public void receiveMessage1(Message message){
        MessageProperties messageProperties=message.getMessageProperties(); // 消息头
        byte[] body=message.getBody(); // 消息体
        CategoryEntity categoryEntity= JSON.parseObject(body,CategoryEntity.class); // 反序列化
        log.info("消息头：{}，消息体：{}",JSON.toJSONString(messageProperties),JSON.toJSONString(categoryEntity));
    }

    /**
     * @description: 监听rabbit队列里的消息(用具体对象接，自动解析)
     * 1. @RabbitListener(queues = {"hello-java-queue"}) :queues是一个数组（可以监听多个队列），写要监听队列的名字
     * 2. 参数可写以下类型：
     * Message message： 使用原生Message（含消息头+消息体）接收
     * CategoryEntity content： 使用具体类接收（和发送的消息类保持一致）
     * Channel channel：通道
     *
     * 3.Queue: 可以有很多人监听，只要收到消息，队列删除消息，且只能有一个收到此消息
     *
     *
     **/

    //@RabbitListener(queues = {"hello-java-queue"})
    public void receiveMessage1(Message message, CategoryEntity content, Channel channel) throws InterruptedException {
        log.info("接收到消息:{}",JSON.toJSONString(content));
        // 休眠3s模拟业务处理
        //Thread.sleep(3000);
        log.info("消息处理完成:{}：{}",JSON.toJSONString(content));
    }

    @RabbitHandler
    public void receiveMessage2(Message message,CategoryEntity content,Channel channel) throws IOException {
        log.info("接收到消息:{}",JSON.toJSONString(content));
        log.info("处理中");
        log.info("消息处理完成:{}",JSON.toJSONString(content));
        // deliveryTag在channel内是顺序自增的
        long deliveryTag=message.getMessageProperties().getDeliveryTag();
        log.info("deliveryTag:{}",deliveryTag);
        // 签收消息
        try {
            // 签收
            if(deliveryTag%2==0){
                channel.basicAck(deliveryTag,false); // false：只签收当前消息（非批量确认）
                log.info("签收了消息：{}",deliveryTag);
            }else{ // 拒收消息
                /**
                 1. basicNack里的参数
                 long deliveryTag: 拒收消息的标签
                boolean var3： 是否批量拒收
                boolean var4：消息是否重新入队,true:发回MQ服务器，让消息重新入队 false: 丢弃
                 **/
                channel.basicNack(deliveryTag,false,false);
                /**
                 2. basicReject里的参数
                 boolean var1： 是否批量拒收
                 boolean var3：消息是否重新入队,true:发回MQ服务器，让消息重新入队 false: 丢弃
                 **/
                //channel.basicReject();
                log.info("拒收消息直接丢弃：{}",deliveryTag);
            }
        }catch (Exception e){
            // 签收过程中网络中断
            log.info("签收过程中网络中断，deliveryTag: {}",deliveryTag);
        }

    }

    @RabbitHandler
    public void receiveMessage3(BrandEntity content){
        log.info("接收到消息:{}",JSON.toJSONString(content));
        log.info("处理中");
        log.info("消息处理完成:{}",JSON.toJSONString(content));
    }
}
