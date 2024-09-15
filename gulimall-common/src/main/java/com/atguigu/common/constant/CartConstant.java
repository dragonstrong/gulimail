package com.atguigu.common.constant;
/**
 * @Author qiang.long
 * @Date 2024/09/13
 * @Description 购物车常量
 **/
public class CartConstant {
    /**
     * 临时用户的user-key (在cookie中)
     **/
    public static final String TEMP_USER_COOKIE_NAME="user-key";
    /**
     * 临时用户cookie过期时间，单位s
     **/
    public static final int TEMP_USER_COOKIE_TIMEOUT=30*24*3600;
    /**
     * 购物车key前缀(redis)
     **/
    public static final String CART_PREFIX="gulimall:cart:";
}
