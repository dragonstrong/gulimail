package com.atguigu.product.service.impl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.product.dao.SkuInfoDao;
import com.atguigu.product.entity.SkuInfoEntity;
import com.atguigu.product.service.SkuInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
@Slf4j
@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }
    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> queryWrapper=new QueryWrapper<SkuInfoEntity>();
        if(params.get("brandId")!=null&&!"0".equals(params.get("brandId"))){ // 不选前端默认发0
            queryWrapper.eq("brand_id",params.get("brandId"));
        }
        if(params.get("catelogId")!=null&&!"0".equals(params.get("catelogId"))){ // 不选前端默认发0
            queryWrapper.eq("catalog_id",params.get("catelogId"));
        }
        if(params.get("min")!=null&&params.get("max")!=null){ // 价格检索区间
            try {
                BigDecimal min=new BigDecimal((String) params.get("min"));
                BigDecimal max=new BigDecimal((String) params.get("max"));
                if(min.compareTo(BigDecimal.ZERO)!=0||max.compareTo(BigDecimal.ZERO)!=0){
                    queryWrapper.ge("price",params.get("min"));
                    queryWrapper.le("price",params.get("max"));
                }
            }catch (Exception e){
                log.info("输入的检索价格格式错误,min={},max={}",params.get("min"),params.get("max"));
            }
        }

        if(params.get("key")!=null){
            String key=(String) params.get("key");
            if(!StringUtil.isEmpty(key)){
                queryWrapper.and(obj->{
                    obj.eq("sku_id",key).or().like("sku_name",key);
                });
            }
        }

        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }
}