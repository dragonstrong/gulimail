package com.atguigu.product.dto.rabbit;
import com.atguigu.product.entity.CategoryEntity;
import lombok.Data;

import java.util.Map;
/**
 * @Author qiang.long
 * @Date 2024/07/18
 * @Description rabbitMQ消息
 **/
@Data
public class MessageDTO {
    /**
     * 给哪个交换机发
     **/
    private String exchange;
    /**
     * 路由键
     **/
    private String routingKey;
    /**
     * 数量
     **/
    private int count;
    /**
     * 消息体
     **/
    private CategoryEntity categoryEntity;
}
