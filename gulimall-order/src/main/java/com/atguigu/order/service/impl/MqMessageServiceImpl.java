package com.atguigu.order.service.impl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.order.dao.MqMessageDao;
import com.atguigu.order.entity.MqMessageEntity;
import com.atguigu.order.service.MqMessageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("mqMessageService")
public class MqMessageServiceImpl extends ServiceImpl<MqMessageDao, MqMessageEntity> implements MqMessageService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MqMessageEntity> page = this.page(
                new Query<MqMessageEntity>().getPage(params),
                new QueryWrapper<MqMessageEntity>()
        );

        return new PageUtils(page);
    }

}