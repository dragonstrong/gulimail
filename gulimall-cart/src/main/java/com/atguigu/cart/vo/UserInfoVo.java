package com.atguigu.cart.vo;
import lombok.Data;
/**
 * @Author qiang.long
 * @Date 2024/09/13
 * @Description 用户身份信息
 **/
@Data
public class UserInfoVo {
    /**
     * 用户id 已登录用户
     **/
    private Long userId;
    /**
     * 用户的user-key  未登录用户
     **/
    private String userKey;
    /**
     * 是否为临时用户
     **/
    private boolean tempUser=false;


}
