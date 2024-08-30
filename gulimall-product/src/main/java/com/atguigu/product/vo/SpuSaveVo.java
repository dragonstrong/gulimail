/**
  * Copyright 2024 bejson.com 
  */
package com.atguigu.product.vo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author qiang.long
 * @Date 2024/08/30
 * @Description 发布商品前端提交的大json
 **/
@Data
public class SpuSaveVo {
    /**
     * spu名字
     */
    private String spuName;
    /**
     * spu描述
     */
    private String spuDescription;
    /**
     * 所属三级分类id
     */
    private Long catalogId;
    /**
     * 所属品牌厂商id
     */
    private Long brandId;
    /**
     * 高（长）
     */
    private BigDecimal weight;
    /**
     * 发布状态
     */
    private int publishStatus;
    /**
     * 描述
     */
    private List<String> decript;
    /**
     * 商品图集
     */
    private List<String> images;

    private Bounds bounds;
    /**
     * 基本属性
     */
    private List<BaseAttrs> baseAttrs;
    /**
     * 所有sku组合
     */
    private List<Skus> skus;
    /**
     * 积分
     */
    @Data
    public static class Bounds {
        private BigDecimal buyBounds;
        private BigDecimal growBounds;
    }
    /**
     * 商品的基本属性
     */
    @Data
    public static class BaseAttrs {
        private Long attrId;
        private String attrValues;
        private int showDesc;
    }
    /**
     * sku
     */
    @Data
    public static class Skus {
        /**
         * sku的属性信息
         */
        private List<Attr> attr;
        /**
         * sku名字
         */
        private String skuName;
        /**
         * 价格
         */
        private BigDecimal price;
        /**
         * 标题
         */
        private String skuTitle;
        /**
         * 副标题
         */
        private String skuSubtitle;
        /**
         * 图集
         */
        private List<Images> images;

        private List<String> descar;
        /**
         * 满减
         */
        private int fullCount;
        /**
         * 折扣
         */
        private BigDecimal discount;
        /**
         * 打折状态是否参与其他优惠
         */
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
    }
    @Data
    public static class Attr {
        private Long attrId;
        private String attrName;
        private String attrValue;
    }
    @Data
    public static class Images {
        private String imgUrl;
        /**
         * 1:默认图片
         */
        private int defaultImg;
    }
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
