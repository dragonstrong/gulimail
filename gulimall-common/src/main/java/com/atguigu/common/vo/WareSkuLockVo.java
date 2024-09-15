package com.atguigu.common.vo;
import lombok.Data;

import java.util.List;
/**
 * @Author qiang.long
 * @Date 2024/09/15
 * @Description 库存锁定Vo
 **/
@Data
public class WareSkuLockVo {
    /**
     * 订单号
     **/
    private String orderSn;
    /**
     * 需要锁定的订单项
     **/
    private List<OrderItemVo> locks;

}
