package com.atguigu.authserver.feign;
import com.atguigu.common.utils.Result;
import com.atguigu.common.vo.MemberResponseVo;
import com.atguigu.common.vo.UserLoginVo;
import com.atguigu.common.vo.UserRegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
/**
 * @Author qiang.long
 * @Date 2024/09/11
 * @Description 会员服务远程调用
 **/
@FeignClient("gulimall-member")
public interface MemberFeignService {
    @PostMapping("/member/member/register")
    Result register(@RequestBody UserRegisterVo userRegisterVo);
    @PostMapping("/member/member/login")
    Result<MemberResponseVo> login(@RequestBody UserLoginVo userLoginVo);
}
