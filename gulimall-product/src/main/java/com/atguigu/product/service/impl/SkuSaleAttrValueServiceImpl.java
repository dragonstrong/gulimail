package com.atguigu.product.service.impl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.common.utils.Result;
import com.atguigu.product.dao.SkuSaleAttrValueDao;
import com.atguigu.product.entity.SkuSaleAttrValueEntity;
import com.atguigu.product.service.SkuSaleAttrValueService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }
    @Override
    public Result<List<String>> getSkuSaleAttrsCombine(Long skuId) {
        SkuSaleAttrValueDao dao=this.baseMapper;
        List<String> attrsCombine=dao.getSaleAttrCombineBySkuId(skuId);
        return Result.ok(attrsCombine);
    }
}