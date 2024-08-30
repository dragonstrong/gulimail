package com.atguigu.product.dao;
import com.atguigu.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.product.vo.AttrAttrGroupRelationVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 * 属性&属性分组关联
 * 
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-17
 */
@Mapper
public interface AttrAttrgroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity> {
    /**
     * @description: 一次批量删除
     **/
    void deleteBatch(@Param("attrAttrgroupRelationVos") List<AttrAttrGroupRelationVo>  attrAttrgroupRelationVos);
}
