package com.atguigu.common.exception;
/**
 * @Author qiang.long
 * @Date 2024/03/22
 * @Description 错误码
 **/

/***
 * 错误码和错误信息定义类
 * 1. 错误码定义规则为5为数字
 * 2. 前两位表示业务场景，最后三位表示错误码。例如：100001。10:通用 001:系统未知异常
 * 3. 维护错误码后需要维护错误描述，将他们定义为枚举形式
 * 错误码列表：
 *  10: 通用
 *      001：参数格式校验
 *  11: 商品
 *  12: 订单
 *  13: 购物车
 *  14: 物流
 *  15: 用户
 *  16: 库存
 *
 *
 */
public enum BizCodeEnum {
    UNKNOW_EXCEPTION(10000,"系统未知异常"),
    VAILD_EXCEPTION(10001,"参数格式校验失败"),
    VAILD_SMS_CODE_EXCEPTION(10002,"验证码获取频率太高，请稍后再试"),
    PRODUCT_UP_EXCEPTION(11000,"商品上架异常"),
    PHONE_EXIST_EXCEPTION(15000,"手机号已存在，注册失败"),
    USERNAME_EXIST_EXCEPTION(15001,"用户名已存在，注册失败"),
    USER_NOT_EXIST_EXCEPTION(15002,"用户不存在，登录失败"),
    PASSWORD_INCORRECT_EXCEPTION(15003,"密码错误，登录失败"),
    NO_STOCK_EXCEPTION(16001,"密码错误，登录失败");

    private int code;
    private String msg;
    BizCodeEnum(int code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
