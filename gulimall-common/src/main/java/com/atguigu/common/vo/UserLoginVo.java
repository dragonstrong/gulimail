package com.atguigu.common.vo;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
/**
 * @Author qiang.long
 * @Date 2024/09/11
 * @Description 用户登录信息
 **/
@Data
public class UserLoginVo {
    /**
     * 用户名/手机号/邮箱
     **/
    @NotEmpty(message = "用户名/手机号/邮箱不能为空")
    private String loginIdentity;
    /**
     * 密码
     **/
    @NotEmpty(message = "密码不能为空")
    private String password;
}
