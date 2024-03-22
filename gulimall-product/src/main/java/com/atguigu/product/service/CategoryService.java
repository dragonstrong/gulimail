package com.atguigu.product.service;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-17 23:41:04
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
    /**
     * @description: 查出所有分类以及子分类，并以树形结构组装起来
     * @param:
     * @return: List<CategoryEntity>
     **/
    List<CategoryEntity> getCategoryTree();
}

