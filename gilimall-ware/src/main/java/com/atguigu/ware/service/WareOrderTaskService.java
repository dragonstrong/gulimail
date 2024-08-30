package com.atguigu.ware.service;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.ware.entity.WareOrderTaskEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 库存工作单
 *
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-18 13:53:59
 */
public interface WareOrderTaskService extends IService<WareOrderTaskEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

