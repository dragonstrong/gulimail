package com.atguigu.coupon.service;
import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.coupon.entity.SkuFullReductionEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-18 13:35:28
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);
    /**
     * @description: 保存sku满减信息
     **/
    void saveSkuReduction(SkuReductionTo skuReductionTo);
}

