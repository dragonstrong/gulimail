package com.atguigu.member.service;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.member.entity.IntegrationChangeHistoryEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 积分变化历史记录
 *
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-18 13:42:34
 */
public interface IntegrationChangeHistoryService extends IService<IntegrationChangeHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

