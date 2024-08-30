package com.atguigu.coupon.service;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.coupon.entity.SpuBoundsEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 商品spu积分设置
 *
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-18 13:35:28
 */
public interface SpuBoundsService extends IService<SpuBoundsEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

