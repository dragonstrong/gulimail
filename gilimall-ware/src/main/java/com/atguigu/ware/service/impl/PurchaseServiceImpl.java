package com.atguigu.ware.service.impl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.ware.dao.PurchaseDao;
import com.atguigu.ware.entity.PurchaseDetailEntity;
import com.atguigu.ware.entity.PurchaseEntity;
import com.atguigu.ware.enumration.PurchaseDetailStatusEnum;
import com.atguigu.ware.enumration.PurchaseStatusEnum;
import com.atguigu.ware.service.PurchaseDetailService;
import com.atguigu.ware.service.PurchaseService;
import com.atguigu.ware.service.WareSkuService;
import com.atguigu.ware.vo.MergePurchaseVo;
import com.atguigu.ware.vo.PurchaseFinishVo;
import com.atguigu.ware.vo.PurchaseItemFinishVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {
    @Autowired
    PurchaseDetailService purchaseDetailService;
    @Autowired
    WareSkuService wareSkuService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }
    @Override
    public PageUtils queryPageUnreceive(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().in("status", Arrays.asList(0,1))
        );

        return new PageUtils(page);
    }
    @Transactional
    @Override
    public void merge(MergePurchaseVo mergePurchaseVo) {
        Long  purchaseId=mergePurchaseVo.getPurchaseId();
        if(purchaseId==null){
            // 为空新建一个
            PurchaseEntity purchaseEntity=new PurchaseEntity();
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setStatus(PurchaseStatusEnum.CREATED.getCode());
            save(purchaseEntity);
            purchaseId=purchaseEntity.getId();
        }

        // 将wms_purchase_detail里purchase_id和status修改即可
        Long finalPurchaseId=purchaseId;
        List<Long> items=mergePurchaseVo.getItems();
        List<PurchaseDetailEntity>  purchaseDetailEntities=items.stream().map(item->{
            PurchaseDetailEntity purchaseDetailEntity=new PurchaseDetailEntity();
            purchaseDetailEntity.setId(item);
            purchaseDetailEntity.setPurchaseId(finalPurchaseId);
            purchaseDetailEntity.setStatus(PurchaseStatusEnum.ASSIGNED.getCode());
            return purchaseDetailEntity;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(purchaseDetailEntities);
        // 更新合并后采购单时间
        PurchaseEntity purchase=new PurchaseEntity();
        purchase.setId(purchaseId);
        purchase.setUpdateTime(new Date());
        updateById(purchase);
    }
    /**
     * @description:
     * @param:
     * @param ids 采购单id wms_purchase
     * @return: com.atguigu.common.utils.PageUtils
     **/
    @Transactional
    @Override
    public void receive(List<Long> ids) {
        // 1、确认当前采购单是新建或已分配状态
        List<PurchaseEntity> purchaseEntityList=ids.stream().map(id->{
            PurchaseEntity purchaseEntity=getById(id);
            return purchaseEntity;
        }).filter(purchaseEntity -> purchaseEntity.getStatus()==PurchaseStatusEnum.CREATED.getCode()||purchaseEntity.getStatus()==PurchaseStatusEnum.ASSIGNED.getCode())
                .map(purchase->{
                    purchase.setStatus(PurchaseStatusEnum.RECEIVE.getCode()); //设为已领取
                    purchase.setUpdateTime(new Date());
                    return purchase;
                }).collect(Collectors.toList());

        // 2、更新采购单状态：->已领取
        updateBatchById(purchaseEntityList);

        // 3、更新采购单所有采购项（wms_purchase_detail）的状态：->正在采购
        List<Long> purchaseIds=purchaseEntityList.stream().map(PurchaseEntity::getId).collect(Collectors.toList());
        purchaseDetailService.updateBatchStatus(purchaseIds);
    }
    @Transactional
    @Override
    public void finish(PurchaseFinishVo purchaseFinishVo) {
        // 1、改变采购单中每个采购项的状态
        boolean flag=true;
        List<PurchaseItemFinishVo> itemFinishVos=purchaseFinishVo.getItems();
        List<PurchaseDetailEntity> update=new ArrayList<>();
        for(PurchaseItemFinishVo p:itemFinishVos){
            PurchaseDetailEntity purchaseDetailEntity=new PurchaseDetailEntity();
            if(p.getStatus()==PurchaseDetailStatusEnum.FAIL.getCode()){
                flag=false;
                purchaseDetailEntity.setStatus(PurchaseDetailStatusEnum.FAIL.getCode());
            }else{
                purchaseDetailEntity.setStatus(PurchaseDetailStatusEnum.FINISH.getCode());
                // 2、将成功的采购项入库
                PurchaseDetailEntity detail=purchaseDetailService.getById(p.getItemId());
                wareSkuService.addStock(detail.getSkuId(),detail.getWareId(),detail.getSkuNum());
            }
            purchaseDetailEntity.setId(p.getItemId());
            update.add(purchaseDetailEntity);
        }
        purchaseDetailService.updateBatchById(update);

        // 3、改变采购单状态
        PurchaseEntity purchaseEntity=new PurchaseEntity();
        purchaseEntity.setId(purchaseFinishVo.getId());
        purchaseEntity.setStatus(flag?PurchaseStatusEnum.FINISH.getCode() : PurchaseStatusEnum.ERROR.getCode());
        purchaseEntity.setUpdateTime(new Date());
        updateById(purchaseEntity);

    }
}