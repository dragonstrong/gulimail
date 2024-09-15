package com.atguigu.order.feign;
import com.atguigu.common.utils.Result;
import com.atguigu.common.vo.SpuInfoVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
/**
 * @Author qiang.long
 * @Date 2024/09/14
 * @Description
 **/
@FeignClient("gulimall-product")
public interface ProductFeignService {
    /**
     * @description: 根据skuid获取spu信息
     * @param:
     * @param id skuId
     * @return: Result<SpuInfoVo>
     **/
    @GetMapping("/product/spuinfo/skuId/{id}")
    Result<SpuInfoVo> getSpuInfoBySkuId(@PathVariable("id") Long id);
}
