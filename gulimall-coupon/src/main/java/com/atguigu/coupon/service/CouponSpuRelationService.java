package com.atguigu.coupon.service;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.coupon.entity.CouponSpuRelationEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 优惠券与产品关联
 *
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-18 13:35:28
 */
public interface CouponSpuRelationService extends IService<CouponSpuRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

