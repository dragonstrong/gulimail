package com.atguigu.order.feign;
import com.atguigu.common.utils.Result;
import com.atguigu.common.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
/**
 * @Author qiang.long
 * @Date 2024/09/14
 * @Description 购物车远程调用
 **/
@FeignClient("gulimall-cart")
public interface CartFeignService {
    /**
     * @description: 获取当前用户被选中的购物项
     * @param:
     * @return: Result<List<CartItem>>
     **/
    @GetMapping("/currentUserCartItems")
    Result<List<OrderItemVo>> getCurrentUserCheckedCartItems();
}
