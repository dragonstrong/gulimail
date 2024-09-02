package com.atguigu.product.service.impl;
import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.common.to.SpuBoundTo;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.common.utils.R;
import com.atguigu.common.utils.Result;
import com.atguigu.common.vo.SkuHasStockVo;
import com.atguigu.product.dao.*;
import com.atguigu.product.entity.*;
import com.atguigu.product.enumeration.PublishStatusEnum;
import com.atguigu.product.feign.CouponFeignService;
import com.atguigu.product.feign.SearchFeignService;
import com.atguigu.product.feign.WareFeignService;
import com.atguigu.product.service.*;
import com.atguigu.product.vo.SpuSaveVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jodd.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    SpuInfoDescDao spuInfoDescDao;
    @Autowired
    SpuImagesService spuImagesService;
    @Autowired
    AttrDao attrDao;
    @Autowired
    ProductAttrValueService productAttrValueService;
    @Autowired
    SkuInfoDao skuInfoDao;
    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    CouponFeignService couponFeignService;
    @Autowired
    BrandDao brandDao;
    @Autowired
    CategoryDao categoryDao;
    @Autowired
    WareFeignService wareFeignService;
    @Autowired
    SearchFeignService searchFeignService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );
        return new PageUtils(page);
    }
    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo spuSaveVo) {
        /**
         * 1.保存spu基本信息 pms_spu_info
         **/
        SpuInfoEntity spuInfoEntity=new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVo,spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        saveBaseSpuInfo(spuInfoEntity);

        /**
         * 2.保存spu描述图片 pms_spu_info_desc
         **/
        List<String> description=spuSaveVo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity=new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId()); // 自增主键保存后获取
        spuInfoDescEntity.setDecript(String.join(",",description));//  逗号连接
        spuInfoDescDao.insert(spuInfoDescEntity);

        /**
         * 3.保存spu的图片集 pms_spu_images
         **/
        List<String> images=spuSaveVo.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(),images);

        /**
         * 4.保存spu的规格参数（基本）pms_product_attr_value
         **/
        List<SpuSaveVo.BaseAttrs> baseAttrs=spuSaveVo.getBaseAttrs();
        List<ProductAttrValueEntity> productAttrValueEntityList=baseAttrs.stream().map(attr->{
            ProductAttrValueEntity productAttrValueEntity=new ProductAttrValueEntity();
            productAttrValueEntity.setSpuId(spuInfoEntity.getId());
            productAttrValueEntity.setAttrId(attr.getAttrId());
            String attrName=Optional.ofNullable(attrDao.selectById(attr.getAttrId())).map(AttrEntity::getAttrName).orElse("");
            productAttrValueEntity.setAttrName(attrName);
            productAttrValueEntity.setAttrValue(attr.getAttrValues());
            productAttrValueEntity.setQuickShow(attr.getShowDesc());
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveBatch(productAttrValueEntityList);

        /**
         * 5.保存当前spu对应的sku信息
         *      5.1.sku的基本信息 pms_sku_info
         *      5.2 sku图片集 pms_sku_images
         *      5.3 sku的销售属性 pms_sku_sale_attr_value
         *      5.4 sku的优惠满减信息：会员系统gulimall_sms-> sms_sku_ladder(商品阶梯价格)->sms_sku_full_reduction(商品满减信息)->sms_member_price(商品会员价格)
         **/
        // 5.1.sku的基本信息
        List<SpuSaveVo.Skus> skuses=spuSaveVo.getSkus();
        if(skuses!=null&&!skuses.isEmpty()){
            skuses.forEach(sku->{
                String defaultImg="";
                for(SpuSaveVo.Images img:sku.getImages()){
                    if(img.getDefaultImg()==1){
                        defaultImg=img.getImgUrl();
                        break;
                    }
                }

                SkuInfoEntity skuInfoEntity=new SkuInfoEntity();
                BeanUtils.copyProperties(sku,skuInfoEntity);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L); // 初始销量置0
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                skuInfoDao.insert(skuInfoEntity);
                //5.2 sku图片集
                // TODO 没有图片路径的不保存
                List<SkuImagesEntity> skuImagesEntityList=sku.getImages().stream().map(imgage->{
                    SkuImagesEntity skuImagesEntity=new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuInfoEntity.getSkuId());
                    skuImagesEntity.setImgUrl(imgage.getImgUrl());
                    skuImagesEntity.setDefaultImg(imgage.getDefaultImg());
                    return skuImagesEntity;
                }).filter(skuImagesEntity -> !StringUtil.isEmpty(skuImagesEntity.getImgUrl())).collect(Collectors.toList());
                skuImagesService.saveBatch(skuImagesEntityList);
                //5.3 sku的销售属性
                List<SpuSaveVo.Attr> attrs=sku.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntityList=attrs.stream().map(attr->{
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity=new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(attr,skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuInfoEntity.getSkuId());
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntityList);
                //5.4 sku的优惠满减信息：会员系统gulimall_sms-> sms_sku_ladder(商品阶梯价格)->sms_sku_full_reduction(商品满减信息)->sms_member_price(商品会员价格)
                SkuReductionTo skuReductionTo=new SkuReductionTo();
                skuReductionTo.setSkuId(skuInfoEntity.getSkuId());
                BeanUtils.copyProperties(sku,skuReductionTo);
                if(skuReductionTo.getFullCount()>0||skuReductionTo.getFullPrice().compareTo(BigDecimal.ZERO)>0){
                    R r=couponFeignService.saveSkuReduction(skuReductionTo);
                    if(r.getCode()!=0){
                        log.error("调用gulimall-coupon远程服务保存sku的优惠满减信息失败");
                    }
                }
            });
        }

        /**
         * 6.保存spu的积分信息：会员系统 gulimall_sms->sms_spu_bounds
         **/
        SpuSaveVo.Bounds bounds=spuSaveVo.getBounds();
        SpuBoundTo spuBoundTo=new SpuBoundTo();
        BeanUtils.copyProperties(bounds,spuBoundTo);
        spuBoundTo.setSpuId(spuInfoEntity.getId());
        R r=couponFeignService.saveSpuBounds(spuBoundTo);
        if(r.getCode()!=0){
            log.error("调用gulimall-coupon远程服务保存spu积分信息失败");
        }
    }
    /**
     * @description: 保存spu基本信息 pms_spu_info
     **/
    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        getBaseMapper().insert(spuInfoEntity);

    }
    @Override
    public PageUtils queryPageByContion(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity>  queryWrapper=new QueryWrapper<SpuInfoEntity>();
        if(params.get("brandId")!=null&&!"0".equals(params.get("brandId"))){ // 不选前端默认发0
            queryWrapper.eq("brand_id",params.get("brandId"));
        }
        if(params.get("catelogId")!=null&&!"0".equals(params.get("catelogId"))){ // 不选前端默认发0
            queryWrapper.eq("catalog_id",params.get("catelogId"));
        }
        if(params.get("status")!=null){
            queryWrapper.eq("publish_status",params.get("status"));
        }

        if(params.get("key")!=null){
            String key=(String) params.get("key");
            if(!StringUtil.isEmpty(key)){
                queryWrapper.and(obj->{
                    obj.eq("id",key).or().like("spu_name",key);
                });
            }
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }
    @Override
    public void up(Long spuId) {
        // 1.查询当前sku所有用来检索的规格属性-共一个spu
        List<ProductAttrValueEntity> productAttrValueEntities=productAttrValueService.getSearchAttrs(spuId);
        List<SkuEsModel.Attr> searchAttrs=productAttrValueEntities.stream().map(productAttrValueEntity -> {
            SkuEsModel.Attr attr=new SkuEsModel.Attr();
            BeanUtils.copyProperties(productAttrValueEntity,attr);
            return attr;
        }).collect(Collectors.toList());

        // 组装需要的数据
        // 查出当前spuId对应的所有sku
        List<SkuInfoEntity> skus=skuInfoDao.selectList(new QueryWrapper<SkuInfoEntity>().eq("spu_id",spuId));
        List<Long> skuIds=new ArrayList<>();
        if(skus!=null){
            skuIds=skus.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
        }

        Map<Long,Boolean> stockMap=null;
        try {
            // TODO 1.远程调用库存系统查出是否有库存
            Result<List<SkuHasStockVo>> hasStock=wareFeignService.getSkuHasStock(skuIds);
            stockMap=hasStock.getData().stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId,SkuHasStockVo::getHasStock));
        }catch (Exception e){
            log.error("远程调用库存服务出现异常，异常原因：{}",e);
        }
        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuEsModel> skuEsModels=skus.stream().map(sku->{
            SkuEsModel skuEsModel=new SkuEsModel();
            BeanUtils.copyProperties(sku,skuEsModel);
            // skuPrice ,skuImg
            skuEsModel.setSkuPrice(sku.getPrice());
            skuEsModel.setSkuImg(sku.getSkuDefaultImg());
            // hasStock , hotScore
            if(finalStockMap ==null){
                // TODO 后面再处理
                skuEsModel.setHasStock(true); // 调用远程服务出现异常直接置为有库存
            }else{
                skuEsModel.setHasStock(finalStockMap.get(sku.getSkuId()));
            }

            // TODO 2.热度评分， 初始置0
            skuEsModel.setHotScore(0L);

            // TODO 3.查询品牌和分类的信息
            BrandEntity brandEntity=brandDao.selectById(sku.getBrandId());
            if(brandEntity!=null){
                skuEsModel.setBrandName(brandEntity.getName());
                skuEsModel.setBrandImg(brandEntity.getLogo());
            }
            CategoryEntity category=categoryDao.selectById(sku.getCatalogId());
            if(category!=null){
                skuEsModel.setCatalogName(category.getName());
            }
            // 设置检索属性
            skuEsModel.setAttrs(searchAttrs);
            return skuEsModel;
        }).collect(Collectors.toList());
        // 5. 发送给gulimall-search服务将数据保存到ES
        Result result=searchFeignService.productStartUp(skuEsModels);
        if(result.getCode()==0){ // 远程调用成功
            // TODO 修改spu发布状态
            SpuInfoEntity spuInfoEntity=new SpuInfoEntity();
            spuInfoEntity.setId(spuId);
            spuInfoEntity.setPublishStatus(PublishStatusEnum.UP.getCode());
            spuInfoEntity.setUpdateTime(new Date());
            getBaseMapper().updateById(spuInfoEntity);
        }else{
            // 远程调用失败
            // TODO 重复调用？ 接口幂等性 重试机制
            /**
             * Feign的调用流程
             * 1. 构造请求数据，将对象转为json
             * RequestTemplate template = this.buildTemplateFromArgs.create(argv);
             * 2. 发送请求执行(执行成功会解码)
             * executeAndDecode(template, options);
             * 3. 执行请求时有重试机制（默认最大重试次数为5）
             * while(true) {
             *             try {
             *                 return this.executeAndDecode(template, options);
             *             } catch (RetryableException var9) {
             *                     retryer.continueOrPropagate(e);
             *                     throw ex; // 重试器有异常会抛出去
             *                     continue;  // 没异常继续重试
             *             }
             *         }
             **/

        }
    }
}