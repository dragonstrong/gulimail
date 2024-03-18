package com.atguigu.ware.dao;

import com.atguigu.ware.entity.WareInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仓库信息
 * 
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-18 13:53:59
 */
@Mapper
public interface WareInfoDao extends BaseMapper<WareInfoEntity> {
	
}
