package com.atguigu.product.service;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.product.vo.AttrAttrGroupRelationVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-17 23:41:04
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
    /**
     * @description: 批量关联
     **/
    void relation(AttrAttrGroupRelationVo[] attrAttrGroupRelationVos);
    /**
     * @description: 批量删除分组-基本属性关联
     **/
    void delRelation(AttrAttrGroupRelationVo[] attrAttrGroupRelationVos);
}

