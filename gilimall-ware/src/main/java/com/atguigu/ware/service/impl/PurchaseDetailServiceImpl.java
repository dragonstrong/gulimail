package com.atguigu.ware.service.impl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.ware.dao.PurchaseDetailDao;
import com.atguigu.ware.entity.PurchaseDetailEntity;
import com.atguigu.ware.enumration.PurchaseDetailStatusEnum;
import com.atguigu.ware.service.PurchaseDetailService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.crypto.spec.OAEPParameterSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<PurchaseDetailEntity> queryWrapper=new QueryWrapper<PurchaseDetailEntity>();
        if(params.get("wareId")!=null&&!StringUtils.isEmpty((String) params.get("wareId"))){
            queryWrapper.eq("ware_id",params.get("wareId"));
        }
        if(params.get("status")!=null&&!StringUtils.isEmpty((String)params.get("status"))){
            queryWrapper.eq("status",params.get("status"));
        }

        if(params.get("key")!=null&& !StringUtils.isEmpty((String) params.get("key"))){
            String key=(String) params.get("key");
            queryWrapper.and(obj->{
                obj.eq("purchase_id",key).or().eq("sku_id",key);
            });
        }

        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void updateBatchStatus(List<Long> purchaseIds) {
        List<PurchaseDetailEntity> purchaseDetailEntityList=Optional.ofNullable(getBaseMapper().selectList(new QueryWrapper<PurchaseDetailEntity>().in("purchase_id",purchaseIds))).orElse(new ArrayList<>()).stream().map(purchaseDetailEntity -> {
            PurchaseDetailEntity  purchaseDetail=new PurchaseDetailEntity();
            purchaseDetail.setId(purchaseDetailEntity.getId());
            purchaseDetail.setStatus(PurchaseDetailStatusEnum.BUYING.getCode()); // 状态设为正在采购中
            return purchaseDetail;
        }).collect(Collectors.toList());
        updateBatchById(purchaseDetailEntityList);
    }
}