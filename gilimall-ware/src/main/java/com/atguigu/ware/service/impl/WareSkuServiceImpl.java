package com.atguigu.ware.service.impl;
import com.alibaba.fastjson.JSON;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.common.utils.R;
import com.atguigu.ware.dao.WareSkuDao;
import com.atguigu.ware.entity.WareSkuEntity;
import com.atguigu.ware.feign.ProductFeignService;
import com.atguigu.ware.service.WareSkuService;
import com.atguigu.ware.vo.SkuInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@Slf4j
@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {
    @Autowired
    WareSkuDao wareSkuDao;
    @Autowired
    ProductFeignService productFeignService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> queryWrapper=new QueryWrapper<WareSkuEntity>();
        if(params.get("skuId")!=null&& !StringUtils.isEmpty((String) params.get("skuId"))){
            queryWrapper.eq("sku_id",params.get("skuId"));
        }
        if(params.get("wareId")!=null&& !StringUtils.isEmpty((String) params.get("wareId"))){
            queryWrapper.eq("ware_id",params.get("wareId"));
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }
    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        List<WareSkuEntity> wareSkuEntities=wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id",skuId).eq("ware_id",wareId));
        if(wareSkuEntities==null||wareSkuEntities.isEmpty()){ // 原来没有，添加
            WareSkuEntity wareSkuEntity=new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum); // 设置库存
            wareSkuEntity.setStockLocked(0);
            // 远程查询sku名字 失败整个事务无需回滚
            // 1. 自己catch异常
            // TODO 其他方法
            try {
                R r=productFeignService.getSkuInfo(skuId);
                if(r.getCode()==0){ // 失败不回滚，因为名字这个信息不是很重要
                    if(r.get("skuInfo")!=null){
                        Map<String,Object> skuInfoVo= (Map<String,Object>)r.get("skuInfo");
                        wareSkuEntity.setSkuName((String) skuInfoVo.get("skuName"));
                    }
                }
            }catch (Exception e){ // 远程服务调用失败
                log.error("调用商品服务获取skuName出现异常,skuId={}",skuId);
            }
            wareSkuDao.insert(wareSkuEntity);
        }else{ // 原来有，更新库存数量
            wareSkuDao.addStock(skuId,wareId,skuNum);
        }
    }
}