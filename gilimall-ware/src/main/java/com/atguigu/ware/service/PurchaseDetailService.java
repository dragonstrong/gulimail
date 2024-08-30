package com.atguigu.ware.service;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.ware.entity.PurchaseDetailEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 
 *
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-18 13:53:59
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

