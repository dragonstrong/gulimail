package com.atguigu.product.dao;

import com.atguigu.product.entity.BrandEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 品牌
 * 
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-17 23:41:04
 */
@Mapper
public interface BrandDao extends BaseMapper<BrandEntity> {
	
}
