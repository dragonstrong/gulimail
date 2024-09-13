package com.atguigu.member.execption;
/**
 * @Author qiang.long
 * @Date 2024/09/11
 * @Description 用户名已存在
 **/
public class PhoneExistExecption extends RuntimeException{
    public PhoneExistExecption() {
        super("手机号已存在");
    }
}
