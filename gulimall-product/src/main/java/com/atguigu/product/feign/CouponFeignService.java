package com.atguigu.product.feign;
import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.common.to.SpuBoundTo;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
/**
 * @Author qiang.long
 * @Date 2024/08/31
 * @Description 远程调用优惠券服务
 **/
@FeignClient("gulimall-coupon")
public interface CouponFeignService {
    // spuBoundTo只要与远程接口的json数据结构相同即可，不一定非要同一个类（都是先转为json再反序列化）
    @PostMapping("coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);
    @PostMapping("coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
