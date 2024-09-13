package com.atguigu.authserver.web;
import com.alibaba.fastjson.JSON;
import com.atguigu.authserver.feign.MemberFeignService;
import com.atguigu.authserver.feign.ThirdPartyFeignService;
import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.utils.Result;
import com.atguigu.common.vo.MemberResponseVo;
import com.atguigu.common.vo.UserLoginVo;
import com.atguigu.common.vo.UserRegisterVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.atguigu.common.constant.AuthServerConstant.LOGIN_USER;
/**
 * @Author qiang.long
 * @Date 2024/09/11
 * @Description 登录注册
 **/
@Slf4j
@Controller
public class LoginController {
    @Autowired
    ThirdPartyFeignService thirdPartyFeignService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    MemberFeignService memberFeignService;

    /**
     * @description: 发送验证码
     * @param:
     * @param phone 手机号
     * @return: com.atguigu.common.utils.Result
     **/
    @ResponseBody
    @GetMapping("/sms/sendcode")
    public Result sendCode(@RequestParam("phone") String phone) throws Exception {
        ValueOperations<String,String> ops=stringRedisTemplate.opsForValue();
        String redisKey=AuthServerConstant.SMS_CODE_CACHE_PREFIX+phone;
        String redisCode=ops.get(redisKey);
        // redis里有验证码且在60s内：不能重复发送
        if(redisCode!=null){
            long redisTime=Long.parseLong(redisCode.split("_")[1]);
            if(System.currentTimeMillis()-redisTime<60000){
                // 60s 内不能重复发
                return Result.error(BizCodeEnum.VAILD_SMS_CODE_EXCEPTION);
            }
        }

        // 6位随机验证码
        //String code= UUID.randomUUID().toString().substring(0,5);
        Random random=new Random();
        int r=random.nextInt(899999);
        String code=r+100000+"";
        Result result=thirdPartyFeignService.sendCode(phone,code);

        // 接口防刷：前端可以拿到发送验证码的uri ，防止恶意攻击无限调用
        // redis缓存验证码和发送时间
        redisCode=code+"_"+System.currentTimeMillis();
        ops.set(redisKey,redisCode,10, TimeUnit.MINUTES); // sms:code:13717782203 -> 167852  10分钟有效
        return result;
    }
    /**
     * @description: 用户注册
     * @param: 
     * @param userRegisterVo
     * @param bindingResult
     * @param redirectAttributes 重定向携带数据(利用session原理，将数据放在session中，只要跳到下一个页面取出这个数据后，session里的数据就会删掉)
     *                           // TODO 分布式下Session问题
     * @return: java.lang.String
     **/
    @PostMapping("/register")
    public String register(@Valid UserRegisterVo userRegisterVo, BindingResult bindingResult, RedirectAttributes redirectAttributes){
        log.info("注册：{}", JSON.toJSONString(userRegisterVo));
        // 1.注册信息格式校验
        if(bindingResult.hasErrors()){
            log.error("注册信息格式校验失败");
            Map<String ,String > errors=bindingResult.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField,FieldError::getDefaultMessage));
            //model.addAttribute("errors",errors);

            redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.gulimall.com/register.html";  // 校验出错重新到注册页
        }
        // 2.校验验证码
        String code= userRegisterVo.getCode();
        ValueOperations<String,String> ops=stringRedisTemplate.opsForValue();
        String redisKey=AuthServerConstant.SMS_CODE_CACHE_PREFIX+userRegisterVo.getPhone();
        String redisCode=ops.get(redisKey);
        if(redisCode!=null&&code.equals(redisCode.split("_")[0])){
            // 校验通过删除redis中的验证码（令牌机制-保证接口幂等性）
            stringRedisTemplate.delete(redisKey);
            // 3.调会员服务进行注册
            Result r=memberFeignService.register(userRegisterVo);
            if(r.getCode()==0){
                // 3.注册成功回到登录页
                return "redirect:http://auth.gulimall.com/login.html";
            }else{ // 调用失败
                Map<String ,String> errors=new HashMap<>();
                errors.put("msg",r.getMsg());
                redirectAttributes.addAttribute("errors",errors);
                return "redirect:http://auth.gulimall.com/register.html";  // 重新到注册页
            }

        }else{ // 验证码校验失败
            Map<String,String> errors=new HashMap<>();
            errors.put("code","验证码错误");
            redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.gulimall.com/register.html";  // 重新到注册页
        }

    }

    /**
     * @description: 用户登录
     * @param:
     * @param userLoginVo
     * @return: java.lang.String
     **/
    @PostMapping("/login")
    public String register(@Valid UserLoginVo userLoginVo, RedirectAttributes redirectAttributes, HttpSession session){
        Result<MemberResponseVo> r=memberFeignService.login(userLoginVo);
        if(r.getCode()==0){
            MemberResponseVo memberResponseVo=r.getData();
            // 登录成功后，创建session（保存在tomcat中），并将JSESSIONID给浏览器
            // 之后浏览器访问哪个网站就会带上该网站的cookie（内含JSESSIONID）
            //
            session.setAttribute(LOGIN_USER,memberResponseVo);
            log.info("用户登录成功:{}",JSON.toJSONString(memberResponseVo));
            return "redirect:http://gulimall.com";
        }else{
            Map<String,String> errors=new HashMap<>();
            errors.put("msg",r.getMsg());
            redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }

    /**
     * @description: 自定义登录跳转逻辑
     * @param:
     * @param session
     * @return: java.lang.String
     **/
    @GetMapping("/login.html")
    public String loginPage(HttpSession session){
        Object attribute=session.getAttribute(LOGIN_USER);
        if(attribute==null){ // 没登录
            return "login";
        }else{    // 登录过
            return "redirect:http://gulimall.com";
        }
    }
}
