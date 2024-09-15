package com.atguigu.order.interceptor;
import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.vo.MemberResponseVo;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
/**
 * @Author qiang.long
 * @Date 2024/09/13
 * @Description 拦截器
 **/
public class LoginUserInterceptor implements HandlerInterceptor {
    public static ThreadLocal<MemberResponseVo> loginUser=new InheritableThreadLocal<>();
    /**
     * 前置拦截：请求到达controller层，先执行preHandle，再执行controller中的业务逻辑
     *                       拦截未登录的用户
     **/
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession httpSession=request.getSession();  //获取sring session包装后的session
        MemberResponseVo member=(MemberResponseVo)httpSession.getAttribute(AuthServerConstant.LOGIN_USER);
        if(member!=null){ // 登录放行
            loginUser.set(member);
            return true;
        }else{ // 未登录拦截，并跳到登录页面
            response.sendRedirect("http://auth.gulimall.com/login.html");
            return false;
        }
    }
}
