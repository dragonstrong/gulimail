package com.atguigu.product.service.impl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.product.dao.SkuInfoDao;
import com.atguigu.product.dao.SkuSaleAttrValueDao;
import com.atguigu.product.entity.SkuImagesEntity;
import com.atguigu.product.entity.SkuInfoEntity;
import com.atguigu.product.entity.SpuInfoDescEntity;
import com.atguigu.product.service.AttrGroupService;
import com.atguigu.product.service.SkuImagesService;
import com.atguigu.product.service.SkuInfoService;
import com.atguigu.product.service.SpuInfoDescService;
import com.atguigu.product.vo.SkuItemSaleAttrVo;
import com.atguigu.product.vo.SkuItemVo;
import com.atguigu.product.vo.SpuItemAttrGroupVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
@Slf4j
@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {
    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SpuInfoDescService spuInfoDescService;
    @Autowired
    AttrGroupService attrGroupService;
    @Autowired
    SkuSaleAttrValueDao skuSaleAttrValueDao;
    @Autowired
    ThreadPoolExecutor threadPoolExecutor;
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
    @Override
    public SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo=new SkuItemVo();
        // 任务编排：  任务1->(2/3/4) 与  任务5 并行
        CompletableFuture<SkuInfoEntity> infoFuture=CompletableFuture.supplyAsync(()->{
            // 任务1.sku基本信息 pms_sku_info
            SkuInfoEntity skuInfoEntity=getById(skuId);
            skuItemVo.setInfo(skuInfoEntity);
            return skuInfoEntity;
        },threadPoolExecutor);
        // 任务2、3、4都必须等任务1执行完再执行
        CompletableFuture<Void> descFuture=infoFuture.thenAcceptAsync(res->{
            // 任务2：spu介绍信息  pms_spu_images pms_spu_info
            SpuInfoDescEntity spuInfoDescEntity =spuInfoDescService.getById(res.getSpuId());
            skuItemVo.setDesp(spuInfoDescEntity);
        },threadPoolExecutor);
        // 其他任务依赖任务1：等1执行完再执行thenApplyAsync
        CompletableFuture<Void> saleAttrFuture=infoFuture.thenAcceptAsync(res->{
            Long spuId=res.getSpuId();
            // 任务3.spu销售属性组合  pms_attr pms_product_attr_value
            List< SkuItemSaleAttrVo> saleAttrs =skuSaleAttrValueDao.getSaleAttrs(spuId);
            skuItemVo.setSaleAttr(saleAttrs);
        },threadPoolExecutor);
        CompletableFuture<Void> baseAttrFuture=infoFuture.thenAcceptAsync(res->{
            Long catalogId=res.getCatalogId();
            Long spuId=res.getSpuId();
            // 任务4.spu规格参数 pms_product_attr_value
            List<SpuItemAttrGroupVo> attrGroupVos=attrGroupService.getAttrGroupWithAttrs(spuId,catalogId);
            skuItemVo.setGroupAttrs(attrGroupVos);
        },threadPoolExecutor);


        // 任务5.sku图片信息 pms_sku_images
        CompletableFuture<Void> skuImagesFuture=CompletableFuture.runAsync(()->{
            List<SkuImagesEntity> skuImages=skuImagesService.getImagesBySkuId(skuId);
            skuItemVo.setImages(skuImages);
        },threadPoolExecutor);
        // 等所有的都做完
        CompletableFuture.allOf(infoFuture,descFuture,saleAttrFuture,baseAttrFuture,skuImagesFuture).get();
        return skuItemVo;
    }
}