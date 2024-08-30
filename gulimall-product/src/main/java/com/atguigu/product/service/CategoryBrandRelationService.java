package com.atguigu.product.service;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.product.entity.CategoryBrandRelationEntity;
import com.atguigu.product.vo.BrandVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-17 23:41:04
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
    /**
     * @description: 获取品牌下的所有分类
     **/
    List<CategoryBrandRelationEntity> getCategoryBrandRelationByBrand(String brandId);
    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);
    /**
     * @description: 获取分类下的所有品牌
     **/
    List<BrandVo> getBrandsByCatId(Long catId);
}

