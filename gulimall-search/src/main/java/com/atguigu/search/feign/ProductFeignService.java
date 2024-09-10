package com.atguigu.search.feign;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
/**
 * @Author qiang.long
 * @Date 2024/09/08
 * @Description 商品服务远程调用
 **/
@FeignClient("gulimall-product")
public interface ProductFeignService {

    /**
     *  根据属性id获取属性完整信息
     **/
    @GetMapping ("product/attr/info/{attrId}")
    R attrInfo(@PathVariable("attrId") Long attrId);

}
