package com.atguigu.common.vo;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
/**
 * @Author qiang.long
 * @Date 2024/09/11
 * @Description 用户注册信息
 **/
@Data
public class UserRegisterVo {
    /**
     * 用户名
     **/
    @NotEmpty(message = "用户名不能为空")
    @Length(min = 6,max = 18,message = "用户名必须是6-18位字符")
    private String userName;
    /**
     * 密码
     **/
    @NotEmpty(message = "密码不能为空")
    @Length(min = 6,max = 18,message = "密码必须是6-18位字符")
    private String password;
    /**
     * 手机号：第1位为1，第2为3-9，剩下9位0-9
     **/
    @NotEmpty(message = "手机号不能为空")
    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$",message = "手机号格式错误")
    private String phone;
    /**
     * 验证码
     **/
    @NotEmpty(message = "验证码不能为空")
    private String code;
}
