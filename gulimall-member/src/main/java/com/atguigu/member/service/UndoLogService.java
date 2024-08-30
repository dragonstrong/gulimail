package com.atguigu.member.service;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.member.entity.UndoLogEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 
 *
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-18 13:42:34
 */
public interface UndoLogService extends IService<UndoLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

