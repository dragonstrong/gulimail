package com.atguigu.product.vo;
import com.atguigu.product.entity.SkuImagesEntity;
import com.atguigu.product.entity.SkuInfoEntity;
import com.atguigu.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;
/**
 * @Author qiang.long
 * @Date 2024/09/08
 * @Description
 **/
@Data
public class SkuItemVo {
    /**
     * sku基本信息 pms_sku_info
     **/
    SkuInfoEntity info;
    /**
     * 是否有货
     **/
    boolean hasStock=true;
    /**
     * sku图片信息 pms_sku_images
     **/
    List<SkuImagesEntity> images;
    /**
     * spu销售属性组合  pms_attr pms_product_attr_value
     **/
    List<SkuItemSaleAttrVo> saleAttr;
    /**
     * spu介绍信息
     **/
    SpuInfoDescEntity desp;
    /**
     * spuspu规格参数： 基本属性分组
     **/
    List<SpuItemAttrGroupVo> groupAttrs;

}
