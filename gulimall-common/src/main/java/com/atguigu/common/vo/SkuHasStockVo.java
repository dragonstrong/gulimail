package com.atguigu.common.vo;
import lombok.Data;
/**
 * @Author qiang.long
 * @Date 2024/09/02
 * @Description 当前sku是否有库存
 **/
@Data
public class SkuHasStockVo {
    private Long skuId;
    /**
     * 是否有库存
     */
    private Boolean hasStock;

}
