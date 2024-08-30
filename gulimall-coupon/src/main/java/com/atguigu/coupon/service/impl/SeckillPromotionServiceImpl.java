package com.atguigu.coupon.service.impl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.coupon.dao.SeckillPromotionDao;
import com.atguigu.coupon.entity.SeckillPromotionEntity;
import com.atguigu.coupon.service.SeckillPromotionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("seckillPromotionService")
public class SeckillPromotionServiceImpl extends ServiceImpl<SeckillPromotionDao, SeckillPromotionEntity> implements SeckillPromotionService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillPromotionEntity> page = this.page(
                new Query<SeckillPromotionEntity>().getPage(params),
                new QueryWrapper<SeckillPromotionEntity>()
        );

        return new PageUtils(page);
    }

}