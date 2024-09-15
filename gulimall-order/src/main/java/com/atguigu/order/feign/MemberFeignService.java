package com.atguigu.order.feign;
import com.atguigu.common.utils.Result;
import com.atguigu.common.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
/**
 * @Author qiang.long
 * @Date 2024/09/14
 * @Description
 **/
@FeignClient("gulimall-member")
public interface MemberFeignService {
    /**
     * 根据会员id查收获地址列表
     */
    @GetMapping("/member/memberreceiveaddress/{memberId}/addresses")
    Result<List<MemberAddressVo>> getAddressListByMemberId(@PathVariable("memberId") Long memberId);

}
