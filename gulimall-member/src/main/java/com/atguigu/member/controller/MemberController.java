package com.atguigu.member.controller;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;
import com.atguigu.member.entity.MemberEntity;
import com.atguigu.member.enums.ServiceInvocationEnum;
import com.atguigu.member.feign.CouponFeignClient;
import com.atguigu.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Map;
/**
 * 会员
 *
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-18 13:42:34
 */
@Slf4j
@RefreshScope
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;
    @Autowired
    private CouponFeignClient couponFeignClient;
    @Autowired
    private RestTemplate restTemplate;

    @Value("${member.user.name}")
    private String name;
    @Value("${member.user.age}")
    private String age;
    @RequestMapping("/properties")
    public R getProperties(){

        return R.ok().put("name",name).put("age",age);
    }
    /**
     * @description: 服务间调用
     * @param:
     * @param mode 0：OpenFeign  1：RestTemplate
     * @return: com.atguigu.common.utils.R
     **/

    @GetMapping("/coupons")
    public R getCoupons(Integer mode) {
        MemberEntity member=new MemberEntity();
        member.setNickname("张三");
        if(mode== ServiceInvocationEnum.OPENFEIGN.ordinal()){ // OpenFeign
            log.info("使用OpenFeign进行服务调用");
            R coupons=couponFeignClient.member();
            return R.ok().put("member",member).put("coupons",coupons.get("coupons"));
        }else if(mode== ServiceInvocationEnum.RESTTEMPLATE.ordinal()){  // RestTemplate
            log.info("使用RestTemplate进行服务调用");
            ResponseEntity<R> r=restTemplate.exchange("http://localhost:7000/coupon/coupon/member/list", HttpMethod.GET,null,R.class);
            return R.ok().put("member",member).put("coupons",r.getBody().get("coupons"));
        }else {
            log.info("不支持的服务调用方式，mode={}",mode);
            return R.error();
        }
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
