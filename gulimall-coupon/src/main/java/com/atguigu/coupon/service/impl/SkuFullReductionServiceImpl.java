package com.atguigu.coupon.service.impl;
import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.coupon.dao.SkuFullReductionDao;
import com.atguigu.coupon.dao.SkuLadderDao;
import com.atguigu.coupon.entity.MemberPriceEntity;
import com.atguigu.coupon.entity.SkuFullReductionEntity;
import com.atguigu.coupon.entity.SkuLadderEntity;
import com.atguigu.coupon.service.MemberPriceService;
import com.atguigu.coupon.service.SkuFullReductionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {
    @Autowired
    SkuLadderDao skuLadderDao;
    @Autowired
    MemberPriceService memberPriceService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }
    @Override
    public void saveSkuReduction(SkuReductionTo skuReductionTo) {
        //5.4 sku的优惠满减信息：会员系统gulimall_sms
        // sms_sku_ladder(商品阶梯价格)->
        SkuLadderEntity skuLadderEntity=new SkuLadderEntity();
        BeanUtils.copyProperties(skuReductionTo,skuLadderEntity);
        skuLadderEntity.setAddOther(skuReductionTo.getCountStatus()); // 打折状态是否参与其他优惠
        if(skuReductionTo.getFullCount()>0){
            skuLadderDao.insert(skuLadderEntity);
        }

        // sms_sku_full_reduction(商品满减信息)
        SkuFullReductionEntity skuFullReductionEntity=new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTo,skuFullReductionEntity);
        if(skuReductionTo.getFullPrice().compareTo(BigDecimal.ZERO)>0){
            save(skuFullReductionEntity);
        }

        // sms_member_price(商品会员价格)
        List<MemberPriceEntity> memberPriceEntityList = Optional.ofNullable(skuReductionTo.getMemberPrice()).orElse(new ArrayList<>()).stream().map(memberPrice -> {
            MemberPriceEntity memberPriceEntity=new MemberPriceEntity();
            memberPriceEntity.setSkuId(skuReductionTo.getSkuId());
            memberPriceEntity.setMemberLevelId(memberPrice.getId());
            memberPriceEntity.setMemberLevelName(memberPrice.getName());
            memberPriceEntity.setMemberPrice(memberPrice.getPrice());
            memberPriceEntity.setAddOther(1);
            return memberPriceEntity;
        }).filter(item->item.getMemberPrice().compareTo(BigDecimal.ZERO)>0).collect(Collectors.toList());
        memberPriceService.saveBatch(memberPriceEntityList);
    }
}