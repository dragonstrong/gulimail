package com.atguigu.coupon.dao;

import com.atguigu.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-18 13:35:28
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
