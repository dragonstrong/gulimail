package com.atguigu.ware.feign;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
/**
 * @Author qiang.long
 * @Date 2024/09/01
 * @Description 商品服务远程调用
 **/
@FeignClient("gulimall-product")
public interface ProductFeignService {
    /** 也可以直接给网关发
     * @FeignClient("gulimall-gateway")
     * @RequestMapping("api/product/skuinfo/info/{skuId}")
     */
    @GetMapping("product/skuinfo/info/{skuId}")
    R getSkuInfo(@PathVariable("skuId") Long skuId);
}
