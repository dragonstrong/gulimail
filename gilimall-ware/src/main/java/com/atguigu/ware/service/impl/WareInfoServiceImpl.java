package com.atguigu.ware.service.impl;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.common.utils.R;
import com.atguigu.common.utils.Result;
import com.atguigu.common.vo.FareVo;
import com.atguigu.common.vo.MemberAddressVo;
import com.atguigu.ware.dao.WareInfoDao;
import com.atguigu.ware.entity.WareInfoEntity;
import com.atguigu.ware.feign.MemberFeignService;
import com.atguigu.ware.service.WareInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {
    @Autowired
    MemberFeignService memberFeignService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareInfoEntity> queryWrapper=new QueryWrapper<WareInfoEntity>();
        if(params.get("key")!=null){
            String key=(String)params.get("key");
            if(!StringUtils.isEmpty(key)){
                queryWrapper.and(obj->{
                    obj.eq("id",key).or().like("name",key).or().like("address",key)
                            .or().eq("areacode",key);
                });
            }
        }
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }
    @Override
    public Result<FareVo> getFreight(Long addressId) {
        FareVo fareVo=new FareVo();
        R r=memberFeignService.addrInfo(addressId);
        if(r.getCode()==0){
            MemberAddressVo addressVo=r.getData("memberReceiveAddress",new TypeReference<MemberAddressVo>(){});
            if(addressVo!=null){
                // 计算运费，此处从简：直接截取手机号最后一位
                String phone=addressVo.getPhone();
                String substring=phone.substring(phone.length()-1,phone.length());
                fareVo.setAddress(addressVo);
                fareVo.setFare(new BigDecimal(substring));
                return Result.ok(fareVo);
            }else{
                log.error("收获地址不存在,addressId={}",addressId);
                return Result.error();
            }
        }else{
            log.error("远程调用member服务获取收获地址失败,addressId={}",addressId);
            return Result.error();
        }
    }
}