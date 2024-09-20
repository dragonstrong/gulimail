package com.atguigu.order.web;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.MemberAddressVo;
import com.atguigu.common.vo.MemberResponseVo;
import com.atguigu.order.feign.MemberFeignService;
import com.atguigu.order.interceptor.LoginUserInterceptor;
import com.atguigu.order.vo.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.concurrent.ExecutionException;
/**
 * @Author qiang.long
 * @Date 2024/09/20
 * @Description 新增收获地址
 **/
@Controller
public class AddressController {
    @Autowired
    MemberFeignService memberFeignService;
    /**
     * @description: 去结算，生成订单:返回订单确认页数据
     **/
    @PostMapping("/addAddress")
    public String addAddress(MemberAddressVo memberAddressVo){
        MemberResponseVo member=LoginUserInterceptor.loginUser.get();
        memberAddressVo.setMemberId(member.getId());
        memberFeignService.save(memberAddressVo);
        return "redirect:http://cart.gulimall.com/cart.html";
    }
}
