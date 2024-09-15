package com.atguigu.ware.service;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.vo.SkuHasStockVo;
import com.atguigu.common.vo.WareSkuLockVo;
import com.atguigu.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-18 13:53:59
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);
    /**
     * @description:  商品（采购项）入库
     * @param:
     * @param skuId skuID
     * @param wareId 仓库id
     * @param skuNum 数量
     * @return: void
     **/
    void addStock(Long skuId, Long wareId, Integer skuNum);
    /**
     * @description: 批量查询sku是否有库存
     * @param:
     * @param skuIds sku id
     * @return: java.util.List<com.atguigu.ware.vo.SkuHasStockVo>
     **/
    List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds);
    /**
     * @description: 为订单锁定库存
     * @param wareSkuLockVo
     **/
    Boolean orderLockStock(WareSkuLockVo wareSkuLockVo);
}

