package com.atguigu.member.service.impl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.member.dao.GrowthChangeHistoryDao;
import com.atguigu.member.entity.GrowthChangeHistoryEntity;
import com.atguigu.member.service.GrowthChangeHistoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("growthChangeHistoryService")
public class GrowthChangeHistoryServiceImpl extends ServiceImpl<GrowthChangeHistoryDao, GrowthChangeHistoryEntity> implements GrowthChangeHistoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<GrowthChangeHistoryEntity> page = this.page(
                new Query<GrowthChangeHistoryEntity>().getPage(params),
                new QueryWrapper<GrowthChangeHistoryEntity>()
        );

        return new PageUtils(page);
    }

}