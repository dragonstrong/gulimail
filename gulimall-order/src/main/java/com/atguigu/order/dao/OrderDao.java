package com.atguigu.order.dao;

import com.atguigu.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-18 13:49:36
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
