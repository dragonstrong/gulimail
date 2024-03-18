package com.atguigu.product.service;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.product.entity.BrandEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 品牌
 *
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-17 23:41:04
 */
public interface BrandService extends IService<BrandEntity> {

    /**
     * @description: 分页查询
     * @param:
     * @param params
     * @return: com.atguigu.common.utils.PageUtils
     **/
    PageUtils queryPage(Map<String, Object> params);
}

