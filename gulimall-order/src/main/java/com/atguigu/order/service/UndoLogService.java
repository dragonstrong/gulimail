package com.atguigu.order.service;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.order.entity.UndoLogEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 
 *
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-18 13:49:36
 */
public interface UndoLogService extends IService<UndoLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

