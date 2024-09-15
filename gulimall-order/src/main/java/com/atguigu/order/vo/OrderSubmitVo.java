package com.atguigu.order.vo;
import lombok.Data;

import java.math.BigDecimal;
/**
 * @Author qiang.long
 * @Date 2024/09/15
 * @Description 订单提交数据
 **/
@Data
public class OrderSubmitVo {
    /**
     * 收货地址id
     **/
    private Long addrId;

    /**
     * 支付方式
     **/
    private Integer payType;
    // 无需提交需要购买的商品，去购物车再获取一遍
    // 优惠，发票
    /**
     * 防重令牌
     **/
    private String orderToken;

    /**
     * 应付价格：验价 (生成订单页数据和点去结算期间价格可能发生了变化)
     **/
    private BigDecimal payPrice;

    /**
     * 订单备注
     **/
    private String note;
    /**
     * 用户相关信息直接从session中取
     **/

}
