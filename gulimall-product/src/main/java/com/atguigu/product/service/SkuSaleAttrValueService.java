package com.atguigu.product.service;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Result;
import com.atguigu.product.entity.SkuSaleAttrValueEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-17 23:41:04
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);
    /**
     * @description: 获取sku的销售属性组合
     * @param:
     * @param skuId
     * @return: Result<List<String>>
     **/
    Result<List<String>> getSkuSaleAttrsCombine(Long skuId);
}

