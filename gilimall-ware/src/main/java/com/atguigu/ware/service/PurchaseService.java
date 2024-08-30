package com.atguigu.ware.service;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.ware.entity.PurchaseEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 采购信息
 *
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-18 13:53:59
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

