package com.atguigu.product.service;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.product.entity.AttrGroupEntity;
import com.atguigu.product.vo.AttrgroupWithAttrs;
import com.atguigu.product.vo.SpuItemAttrGroupVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-17 23:41:04
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);
    PageUtils MyQueryPage(Map<String, Object> params,Long catelogId);
    /**
     * @description: 根据三级分类ID查出所有属性分组及该分组下的所有属性
     **/
    List<AttrgroupWithAttrs> getGroupsAndAttrsByCatId(Long catelogId);
    /**
     * @description: 根绝spuId和分类id查出所有分组及分组里的所有属性
     * @param:
     * @param spuId
     * @param catalogId
     * @return: java.util.List<com.atguigu.product.vo.SkuItemVo.SpuItemAttrGroupVo>
     **/
    List<SpuItemAttrGroupVo> getAttrGroupWithAttrs(Long spuId, Long catalogId);
}

