package com.atguigu.product.dao;

import com.atguigu.product.entity.AttrEntity;
import com.atguigu.product.vo.AttrVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
/**
 * 商品属性
 * 
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-17 23:41:04
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {
    /**
     * @description: 自增主键插入数据（未携带attr_id）后返回主键(将attr_id设置回attrId)
     **/
    void insertReturnId(@Param("attrEntity") AttrEntity attrEntity);
	
}
