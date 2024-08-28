package com.atguigu.product.dao;

import com.atguigu.product.entity.BrandEntity;
import com.atguigu.product.entity.CategoryBrandRelationEntity;
import com.atguigu.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
/**
 * 品牌分类关联
 * 
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-17 23:41:04
 */
@Mapper
public interface CategoryBrandRelationDao extends BaseMapper<CategoryBrandRelationEntity> {

    /**
     * @description: 更新品牌名（根据品牌id）
     **/
    void updateBrandName(@Param("brandEntity") BrandEntity brandEntity);
    /**
     * @description: 更新分类名（根据分类id）
     **/
    void updateCategoryName(@Param("categoryEntity") CategoryEntity categoryEntity);
}
