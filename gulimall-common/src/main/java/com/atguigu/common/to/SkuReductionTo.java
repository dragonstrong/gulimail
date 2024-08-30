package com.atguigu.common.to;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
/**
 * @Author qiang.long
 * @Date 2024/08/31
 * @Description Sku满减信息
 **/
@Data
public class SkuReductionTo {
    /**
     * sku Id
     */
    private Long skuId;
    /**
     * 满减
     */
    private int fullCount;
    /**
     * 折扣
     */
    private BigDecimal discount;
    private int countStatus;
    /**
     * 原价
     */
    private BigDecimal fullPrice;
    /**
     * 打折价格
     */
    private BigDecimal reducePrice;
    private int priceStatus;
    /**
     * 会员价格（不同等级不同）
     */
    private List<MemberPrice> memberPrice;
    /**
     * 会员价格
     */
    @Data
    public static class MemberPrice {
        /**
         * 会员等级id
         */
        private Long id;
        /**
         * 会员等级名
         */
        private String name;
        /**
         * 会员价格
         */
        private BigDecimal price;
    }
}
