package com.atguigu.common.vo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
/**
 * @Author qiang.long
 * @Date 2024/09/11
 * @Description 订单中的单个订单项（同购物车中的购物项）
 **/
@Data
public class OrderItemVo {
    /**
     * 商品sku id
     **/
    private Long skuId;
    /**
     * 商品标题
     **/
    private String title;
    /**
     * 商品图片
     **/
    private String image;
    /**
     * 商品套餐信息：可能买了很多sku(销售组合)
     **/
    private List<String> skuAttr;
    /**
     * 价格
     **/
    private BigDecimal price;
    /**
     * 数量
     **/
    private Integer count;
    /**
     * 总价格
     **/
    private BigDecimal totalPrice;
    /**
     * 是否有货
     **/
    private boolean hasStock;
    /**
     * 商品重量
     **/
    private BigDecimal weight;
}
