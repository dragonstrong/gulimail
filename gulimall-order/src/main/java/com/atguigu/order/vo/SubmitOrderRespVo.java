package com.atguigu.order.vo;
import com.atguigu.order.entity.OrderEntity;
import lombok.Data;
/**
 * @Author qiang.long
 * @Date 2024/09/15
 * @Description 下单结果返回数据
 **/
@Data
public class SubmitOrderRespVo {
    /**
     * 订单信息
     **/
    private OrderEntity order;
    /**
     * 错误码：0为成功
     **/
    private Integer code;
}
