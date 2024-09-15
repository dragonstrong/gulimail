package com.atguigu.order.feign;
import com.atguigu.common.utils.Result;
import com.atguigu.common.vo.FareVo;
import com.atguigu.common.vo.SkuHasStockVo;
import com.atguigu.common.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
/**
 * @Author qiang.long
 * @Date 2024/09/15
 * @Description
 **/
@FeignClient("gulimall-ware")
public interface WareFeignService {
    /**
     * @description: 批量查询sku是否有库存
     * @param:
     * @param skuIds sku Id集合
     * @return: com.atguigu.common.utils.R
     **/
    @PostMapping("/ware/waresku/hasstock")
    Result<List<SkuHasStockVo>> getSkuHasStock(@RequestBody List<Long> skuIds);

    /**
     * @description: 获取收货地址+运费信息
     * @param:
     * @param addressId 收货地址id
     * @return: Result<BigDecimal>
     **/
    @GetMapping("/ware/wareinfo/fare")
    Result<FareVo> getFreight(@RequestParam("addressId") Long addressId);
    /**
     * @description: 为订单锁定库存
     * @param wareSkuLockVo
     **/
    @PostMapping("/ware/waresku/lock/order")
    Result<Boolean> orderLockStock(@RequestBody WareSkuLockVo wareSkuLockVo);
}
