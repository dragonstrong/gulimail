package com.atguigu.cart.feign;
import com.atguigu.common.utils.R;
import com.atguigu.common.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;
/**
 * @Author qiang.long
 * @Date 2024/09/13
 * @Description gulimall-product远程调用
 **/
@FeignClient("gulimall-product")
public interface ProductFeignService {
    /**
     * 获取sku详情
     **/
    @RequestMapping("/product/skuinfo/info/{skuId}")
    R skuInfo(@PathVariable("skuId") Long skuId);
    /**
     * 获取sku销售属性组合
     **/
    @GetMapping("/product/skusaleattrvalue/saleAttrs/{skuId}")
    Result<List<String>> getSkuSaleAttrsCombine(@PathVariable("skuId") Long skuId);
    /**
     * 获取sku最新价格
     */
    @GetMapping("/product/skuinfo/{skuId}/getPrice")
    Result<BigDecimal> getPrice(@PathVariable("skuId") Long skuId);

}
