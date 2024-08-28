package com.atguigu.product.service.impl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.product.dao.BrandDao;
import com.atguigu.product.dao.CategoryBrandRelationDao;
import com.atguigu.product.entity.BrandEntity;
import com.atguigu.product.entity.CategoryBrandRelationEntity;
import com.atguigu.product.service.BrandService;
import com.atguigu.product.service.CategoryBrandRelationService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jodd.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Queue;
@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {
    @Autowired
    CategoryBrandRelationDao categoryBrandRelationDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<BrandEntity> queryWrapper=new QueryWrapper<BrandEntity>();
        // 根据品牌id或品牌名检索
        String key=(String)params.get("key"); // 检索关键字
        if(!StringUtil.isEmpty(key)){
            queryWrapper.eq("brand_id",key).or().like("name",key);
        }
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }
    @Transactional
    @Override
    public void UpdateDetail(BrandEntity brand) {
        getBaseMapper().updateById(brand);
        categoryBrandRelationDao.updateBrandName(brand); // 更新对应的关联关系表
        // TODO 更新其他关联表
    }
}