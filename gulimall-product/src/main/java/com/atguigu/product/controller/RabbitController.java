package com.atguigu.product.controller;
import com.alibaba.fastjson.JSON;
import com.atguigu.common.utils.R;
import com.atguigu.product.dto.rabbit.ExchangeDTO;
import com.atguigu.product.dto.rabbit.MessageDTO;
import com.atguigu.product.service.RabbitMQService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
/**
 * @Author qiang.long
 * @Date 2024/07/18
 * @Description rabbitMQ测试
 **/
@Slf4j
@RestController()
public class RabbitController {
    @Resource
    private RabbitMQService rabbitMQService;
    @PostMapping("/rabbit/exchange/create")
    public R createExchange(@RequestBody ExchangeDTO exchangeDTO){
        log.info("【创建exchanges：{}】", JSON.toJSONString(exchangeDTO));
        rabbitMQService.createExchange(exchangeDTO);
        return R.ok("创建成功").put("exchange",exchangeDTO.getName());
    }

    //@PostMapping("/rabbit/message")
    public R sendMessage(@RequestBody MessageDTO messageDTO){
        log.info("【发送消息：{}】", JSON.toJSONString(messageDTO));
        rabbitMQService.sendMessage(messageDTO);
        return R.ok("发送成功");
    }

    @PostMapping("/rabbit/message")
    public R postMessage(@RequestParam int num){
        rabbitMQService.sendMessage2(num);
        return R.ok("发送成功");

    }
}
