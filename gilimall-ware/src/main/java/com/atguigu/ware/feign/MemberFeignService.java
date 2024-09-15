package com.atguigu.ware.feign;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
/**
 * @Author qiang.long
 * @Date 2024/09/15
 * @Description
 **/
@FeignClient("gulimall-member")
public interface MemberFeignService {
    /**
     * 查收获地址信息
     */
    @RequestMapping("/member/memberreceiveaddress/info/{id}")
    R addrInfo(@PathVariable("id") Long id);
}
