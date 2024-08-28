package com.atguigu.product.service.impl;
import com.alibaba.fastjson.JSON;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.product.dao.BrandDao;
import com.atguigu.product.dao.CategoryBrandRelationDao;
import com.atguigu.product.dao.CategoryDao;
import com.atguigu.product.entity.BrandEntity;
import com.atguigu.product.entity.CategoryBrandRelationEntity;
import com.atguigu.product.entity.CategoryEntity;
import com.atguigu.product.service.CategoryBrandRelationService;
import com.atguigu.product.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
@Slf4j
@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    BrandDao brandDao;
    @Autowired
    CategoryDao categoryDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }
    /**
     * @description: 根据品牌厂商id查询分类列表
     * @param:
     * @param brandId
     * @return: java.util.List<com.atguigu.product.entity.CategoryEntity>
     **/
    @Override
    public List<CategoryBrandRelationEntity> getCategoryBrandRelationByBrand(String brandId) {
        // 一个品牌厂商可能涉及多个领域：如华为下面有手机、笔记本、电视
        return getBaseMapper().selectList(new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id",brandId));
    }
    /**
     * @description: 保存关联关系
     **/
    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        // 有关联记录不重复关联
        CategoryBrandRelationEntity relation=getBaseMapper().selectOne(new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id",categoryBrandRelation.getBrandId()).eq("catelog_id",categoryBrandRelation.getCatelogId()));
        if(relation==null){
            // 可能没有传品牌和分类的名字
            String brandName= Optional.ofNullable(brandDao.selectById(categoryBrandRelation.getBrandId())).map(BrandEntity::getName).orElse("");
            String categoryName=Optional.ofNullable(categoryDao.selectById(categoryBrandRelation.getCatelogId())).map(CategoryEntity::getName).orElse("");
            categoryBrandRelation.setBrandName(brandName);
            categoryBrandRelation.setCatelogName(categoryName);
            save(categoryBrandRelation);
        }else{
            log.info("关联{}已存在", JSON.toJSONString(categoryBrandRelation));
        }
    }
}