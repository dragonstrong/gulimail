package com.atguigu.member.execption;
/**
 * @Author qiang.long
 * @Date 2024/09/11
 * @Description 用户名已存在异常
 **/
public class UserNameExistExecption extends RuntimeException{
    public UserNameExistExecption() {
        super("用户名已存在");

    }
}
