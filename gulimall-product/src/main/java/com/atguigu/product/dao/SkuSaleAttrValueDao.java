package com.atguigu.product.dao;

import com.atguigu.product.entity.SkuSaleAttrValueEntity;
import com.atguigu.product.vo.SkuItemSaleAttrVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 * sku销售属性&值
 * 
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-17 23:41:04
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {
    List<SkuItemSaleAttrVo> getSaleAttrs(@Param("spuId") Long spuId);
    /**
     * 获取sku销售属性组合：例如黑色8G+256G
     **/
    List<String> getSaleAttrCombineBySkuId(@Param("skuId") Long skuId);
}
