package com.atguigu.product.service;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Result;
import com.atguigu.product.entity.SpuInfoEntity;
import com.atguigu.product.vo.SpuSaveVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * spu信息
 *
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-17 23:41:04
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
    /**
     * @description: 保存上架的商品信息
     **/
    void saveSpuInfo(SpuSaveVo spuSaveVo);
    void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity);
    /**
     * @description: spu管理-按条件查询
     **/
    PageUtils queryPageByContion(Map<String, Object> params);
    /**
     * @description: spu上架
     **/
    void up(Long spuId);
    /**
     * @description: 根据skuid获取spu信息
     * @param id skuId
     **/
    Result<SpuInfoEntity> getSpuInfoBySkuId(Long id);
}

