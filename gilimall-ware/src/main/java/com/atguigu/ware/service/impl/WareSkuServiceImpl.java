package com.atguigu.ware.service.impl;
import com.atguigu.common.exception.NoStockExecption;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.OrderItemVo;
import com.atguigu.common.vo.SkuHasStockVo;
import com.atguigu.common.vo.WareSkuLockVo;
import com.atguigu.ware.dao.WareSkuDao;
import com.atguigu.ware.entity.WareSkuEntity;
import com.atguigu.ware.feign.ProductFeignService;
import com.atguigu.ware.service.WareSkuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
                if(r.getCode()==0){ // 失败不回滚，名字不是很重要
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
    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        return skuIds.stream().map(skuId->{
            SkuHasStockVo skuHasStockVo=new SkuHasStockVo();
            // 一个sku可能分布在多个库存，因此  剩余量=总量-总锁定量
            // SELECT SUM(stock-stock_locked) FROM `wms_ware_sku` WHERE sku_id=#{skuId};
            skuHasStockVo.setSkuId(skuId);
            Long stock=wareSkuDao.getSkuStock(skuId);
            skuHasStockVo.setHasStock(stock!=null&&stock>0); // 有些可能没入库存，stock为null
            return skuHasStockVo;
        }).collect(Collectors.toList());
    }

    /**
     * @description: 为订单锁定库存：只要有一个商品锁定失败就回滚
     * Transactional 默认运行时异常就会回滚
     **/
    @Transactional(rollbackFor= NoStockExecption.class)
    @Override
    public Boolean orderLockStock(WareSkuLockVo wareSkuLockVo) {
        // 1.查出有库存的所有仓库id（实际是锁最近的仓库, 这里简化）
        List<OrderItemVo> locks=wareSkuLockVo.getLocks();
        if(locks!=null){
            List<SkuWareHasStock> skuWareHasStocks=locks.stream().map(orderItemVo -> {
                SkuWareHasStock stock=new SkuWareHasStock();
                stock.setSkuId(orderItemVo.getSkuId());
                stock.setNum(orderItemVo.getCount());
                List<Long> wareIds=wareSkuDao.listWareIdsHasStock(orderItemVo.getSkuId());
                stock.setWareIds(wareIds);
                return stock;
            }).collect(Collectors.toList());
            // 2. 锁定库存
            boolean lockedAll=true; // 是否全部锁成功（一个失败全部失败）
            for(SkuWareHasStock wareHasStock:skuWareHasStocks){
                boolean locked=false;
                List<Long> wardIds=wareHasStock.getWareIds();
                if(wardIds==null||wardIds.isEmpty()){ // 该商品没有库存
                    lockedAll=false;
                    throw new NoStockExecption(wareHasStock.getSkuId());
                }else{
                    for(Long wareId:wardIds){
                        Long count=wareSkuDao.lockSkuStock(wareHasStock.getSkuId(),wareId,wareHasStock.getNum());
                        if(count==1){ // 当前仓库锁定成功退出，锁定失败继续下一仓库
                            locked=true;
                            break;
                        }
                    }
                    if(!locked){ // 只要一个商品锁定失败就整体失败
                        lockedAll=false;
                        throw new NoStockExecption(wareHasStock.getSkuId());
                    }
                }
            }
        }
        // 所有商品都锁定成功
        return true;
    }

    @Data
    class SkuWareHasStock{
        /**
         * 商品id
         **/
        private Long skuId;
        /**
         * 需要锁定的数量
         **/
        private Integer num;
        /**
         * 有库存的仓库id
         **/
        private List<Long> wareIds;
    }
}