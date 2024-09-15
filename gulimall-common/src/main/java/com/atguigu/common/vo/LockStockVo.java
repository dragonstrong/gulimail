package com.atguigu.common.vo;
import lombok.Data;
/**
 * @Author qiang.long
 * @Date 2024/09/15
 * @Description 库存锁定结果
 **/
@Data
public class LockStockVo {
    /**
     * 商品sku id
     **/
    private Long skuId;
    /**
     * 数量
     **/
    private Integer num;

    /**
     * 是否锁定成功
     **/
    private Boolean locked;
}
