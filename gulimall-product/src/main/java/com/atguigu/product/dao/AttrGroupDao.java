package com.atguigu.product.dao;

import com.atguigu.product.entity.AttrGroupEntity;
import com.atguigu.product.vo.SpuItemAttrGroupVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 * 属性分组
 * 
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-17 23:41:04
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {
    /**
     * @description:  查询属性分组和分组下所有属性
     * @param:
     * @param spuId
     * @param catalogId
     * @return: void
     **/
    List<SpuItemAttrGroupVo> getAttrGroupWithAttrs(@Param("spuId") Long spuId, @Param("catalogId") Long catalogId);
}
