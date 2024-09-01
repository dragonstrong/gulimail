package com.atguigu.product.service.impl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.product.dao.ProductAttrValueDao;
import com.atguigu.product.entity.ProductAttrValueEntity;
import com.atguigu.product.service.ProductAttrValueService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }
    @Override
    public List<ProductAttrValueEntity> baseAttrlistForSpu(Long spuId) {
        return getBaseMapper().selectList(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id",spuId));
    }
    @Transactional
    @Override
    public void updateBaseAttrForSpu(Long spuId,List<ProductAttrValueEntity> productAttrValueEntities) {
        // 删除该spuId的所有属性值，然后重新添加
        getBaseMapper().delete(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id",spuId));
        List<ProductAttrValueEntity> collect=productAttrValueEntities.stream().map(productAttrValueEntity -> {
            productAttrValueEntity.setSpuId(spuId);
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        saveBatch(collect);
    }
}