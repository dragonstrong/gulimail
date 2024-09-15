package com.atguigu.order.enumration;
import lombok.Getter;
/**
 * @Author qiang.long
 * @Date 2024/09/16
 * @Description 提交订单错误码
 **/
@Getter
public enum OrderSubmitErrorEnum {
    SUCCESS(0,"订单提交成功"),
    TOKEN_INVALID(1,"订单过期,令牌校验失败，请刷新再次提交"),
    PRICE_CKECK_FAIL(2,"验价失败"),
    STOCK_LOCK_FAIL(3,"库存锁定失败");

    Integer code;
    String val;
    OrderSubmitErrorEnum(Integer code,String val){
        this.code=code;
        this.val=val;
    }
    public static String getMsgByCode(Integer code){
        for(OrderSubmitErrorEnum orderSubmitErrorEnum:OrderSubmitErrorEnum.values()){
            if(code.equals(orderSubmitErrorEnum.getCode())){
                return orderSubmitErrorEnum.getVal();
            }
        }
        return "";
    }
}
