package com.atguigu.product.service;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.product.entity.CategoryEntity;
import com.atguigu.product.vo.Catalog2Vo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Arrays;
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

    /**
     * @description: p138 获取二级三级分类数据
     * @param:
     * @return: List<Catalog2Vo>>
     **/
    Map<String, List<Catalog2Vo>> getCatalogJsonByDB();

    /**
     * @description: p138 获取二级三级分类数据 加上SpringCache 缓存注解
     * @param:
     * @return: List<Catalog2Vo>>
     **/
    Map<String, List<Catalog2Vo>> getCatalogJsonByDBUseSpringCache();

    /**
     * @description: p138 获取二级三级分类数据 (加入redis)  本地锁
     * @param:
     * @return: List<Catalog2Vo>>
     **/
    Map<String, List<Catalog2Vo>> getCatalogJsonUseRedisWithLocalLock();
    /**
     * @description: p138 获取二级三级分类数据 (加入redis)  分布式锁
     * @param:
     * @return: List<Catalog2Vo>>
     **/
    Map<String, List<Catalog2Vo>> getCatalogJsonWithLock();
    /**
     * @description: 使用Redisson加分布式锁
     * @param:
     * @return: List<Catalog2Vo>>
     **/
     Map<String, List<Catalog2Vo>> getCatalogJsonFromDBWithRedisson() throws InterruptedException;
     /**
      * @description: 更新菜单数据
      * @param:
      * @param category
      * @return: java.lang.String
      **/
    String updateCategory(CategoryEntity category);
    void removeMenuByIds(List<Long> asList);
}

