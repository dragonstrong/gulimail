package com.atguigu.product.service;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.product.entity.ProductAttrValueEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-17 23:41:04
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);
    /**
     * 获取Spu基本属性
     **/
    List<ProductAttrValueEntity> baseAttrlistForSpu(Long spuId);
    /**
     * 更新Spu基本属性值
     **/
    void updateBaseAttrForSpu(Long spuId,List<ProductAttrValueEntity> productAttrValueEntities);
    /**
     * 查询当前spu下所有用来检索的基本属性
     **/
    List<ProductAttrValueEntity> getSearchAttrs(Long spuId);
}

