package com.atguigu.order.vo;
import com.atguigu.order.entity.OrderEntity;
import com.atguigu.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
/**
 * @Author qiang.long
 * @Date 2024/09/15
 * @Description 订单
 **/
@Data
public class OrderCreateVo {
    /**
     * 订单
     **/
    private OrderEntity order;
    /**
     * 订单项
     **/
    private List<OrderItemEntity> orderItems;
    /**
     * 订单计算的应付价格
     **/
    private BigDecimal payPrice;
    /**
     * 运费
     **/
    private BigDecimal fare;

}
