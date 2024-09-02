package com.atguigu.product.feign;
import com.atguigu.common.utils.Result;
import com.atguigu.common.vo.SkuHasStockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
/**
 * @Author qiang.long
 * @Date 2024/09/02
 * @Description guliamll-ware远程调用
 **/
@FeignClient("gulimall-ware")
public interface WareFeignService {
    @PostMapping("ware/waresku/hasstock")
    Result<List<SkuHasStockVo>> getSkuHasStock(@RequestBody List<Long> skuIds);
}
