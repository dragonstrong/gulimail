package com.atguigu.ware.feign;
import com.atguigu.common.utils.Result;
import com.atguigu.common.vo.OrderVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
/**
 * @Author qiang.long
 * @Date 2024/09/18
 * @Description
 **/
@FeignClient("gulimall-order")
public interface OrderFeignService {
    /**
     * @description: 获取订单状态
     * @param:
     * @param orderSn 订单号
     * @return: com.atguigu.common.utils.Result<java.lang.Integer>
     **/
    @GetMapping("/order/order/status/{orderSn}")
    Result<OrderVo> getOrderStatus(@PathVariable("orderSn") String orderSn);
}
