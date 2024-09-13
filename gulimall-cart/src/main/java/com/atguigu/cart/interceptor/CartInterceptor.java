package com.atguigu.cart.interceptor;
import com.atguigu.cart.vo.UserInfoVo;
import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.constant.CartConstant;
import com.atguigu.common.vo.MemberResponseVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;
/**
 * @Author qiang.long
 * @Date 2024/09/13
 * @Description 购物车拦截器
 **/
public class CartInterceptor implements HandlerInterceptor {
    public static ThreadLocal<UserInfoVo> threadLocal=new InheritableThreadLocal<>(); // 同一线程共享访问（私有）
    /**
     * @description: 前置拦截：请求到达controller层，先执行preHandle，再执行controller中的业务逻辑
     *                       判断用户登录状态：登录了设置userId 未登录设置user-key
     * @param:
     * @param request
     * @param response
     * @param handler
     * @return: boolean
     **/
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession httpSession=request.getSession();  //获取sring session包装后的session
        MemberResponseVo member=(MemberResponseVo)httpSession.getAttribute(AuthServerConstant.LOGIN_USER);
        UserInfoVo userInfoVo=new UserInfoVo();
        if(member!=null){ // 已登录：获取用户购物车
            userInfoVo.setUserId(member.getId());
        }
        //  从未登录到已登录切换瞬间：既有member又有cookie(userId和userKey都要设置)
        //  未登录：获取临时购物车
        Cookie[] cookies=request.getCookies();
        if(cookies!=null&&cookies.length>0){
            for(Cookie cookie:cookies){
                String name=cookie.getName();
                if(name.equals(CartConstant.TEMP_USER_COOKIE_NAME)){// cookie中携带了user-key
                    userInfoVo.setUserKey(cookie.getValue());
                    userInfoVo.setTempUser(true);
                    break;
                }
            }
        }
        // cookie中没有携带user-key，为其创建一个并发回浏览器
        if(userInfoVo.getUserKey()==null|| StringUtils.isEmpty(userInfoVo.getUserKey())){
            String uuid= UUID.randomUUID().toString();
            userInfoVo.setUserKey(uuid);
        }

        // ThreadLocal同一线程共享数据
        threadLocal.set(userInfoVo);
        return true; // 全部放行，false为拦截
    }

    /**
     * @description: 后置拦截：目标方法（controller层）执行之后再执行
     * @param:
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @return: void
     **/
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception{
        // 服务器创建user-key后将其返给浏览器（主要针对首次访问时请求cookie中没有携带user-key的情况）
        UserInfoVo userInfoVo=threadLocal.get();
        if(!userInfoVo.isTempUser()){
            Cookie cookie=new Cookie(CartConstant.TEMP_USER_COOKIE_NAME,userInfoVo.getUserKey());
            cookie.setDomain("gulimall.com");
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);
            response.addCookie(cookie);
        }
    }
}
