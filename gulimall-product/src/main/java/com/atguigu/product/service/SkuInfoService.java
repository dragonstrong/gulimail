package com.atguigu.product.service;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.product.entity.SkuInfoEntity;
import com.atguigu.product.vo.SkuItemVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;
import java.util.concurrent.ExecutionException;
/**
 * sku信息
 *
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-17 23:41:04
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
    PageUtils queryPageByCondition(Map<String, Object> params);
    /**
     * @description: 查商品详情
     * @param:
     * @param skuId
     * @return: com.atguigu.product.vo.SkuItemVo
     **/
    SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException;
}

