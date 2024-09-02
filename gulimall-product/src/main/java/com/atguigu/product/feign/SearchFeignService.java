package com.atguigu.product.feign;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
/**
 * @Author qiang.long
 * @Date 2024/09/02
 * @Description gulimall-search远程调用
 **/
@FeignClient("gulimall-search")
public interface SearchFeignService {
    @PostMapping("search/product/up")
    Result productStartUp(@RequestBody List<SkuEsModel> skuEsModels);
}
