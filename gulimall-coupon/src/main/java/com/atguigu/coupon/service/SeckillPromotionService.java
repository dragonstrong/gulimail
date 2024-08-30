package com.atguigu.coupon.service;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.coupon.entity.SeckillPromotionEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 秒杀活动
 *
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-18 13:35:28
 */
public interface SeckillPromotionService extends IService<SeckillPromotionEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

