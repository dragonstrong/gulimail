package com.atguigu.order.service;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Result;
import com.atguigu.order.entity.OrderEntity;
import com.atguigu.order.vo.OrderConfirmVo;
import com.atguigu.order.vo.OrderSubmitVo;
import com.atguigu.order.vo.SubmitOrderRespVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;
import java.util.concurrent.ExecutionException;
/**
 * 订单
 *
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-18 13:49:36
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);
    /**
     * @description: 返回订单确认页数据
     **/
    OrderConfirmVo confirm() throws ExecutionException, InterruptedException;
    /**
     * @description: 下单
     **/
    SubmitOrderRespVo submitOrder(OrderSubmitVo orderSubmitVo);
    /**
     * @description: 获取订单状态
     **/
    Result<OrderEntity> getOrderStatus(String orderSn);
    /**
     * @description: 定时关单
     **/
    void closeOrder(OrderEntity order);
}

