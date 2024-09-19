package com.atguigu.ware.enumration;
import lombok.Getter;
/**
 * @Author qiang.long
 * @Date 2024/09/15
 * @Description 订单状态枚举
 **/
@Getter
public enum OrderStatusEnum {
    NEW(0,"待付款"),
    DELIVER_WAITTING(1,"待发货"),
    DELIVER_FINISH(2,"已发货"),
    FINISH(3,"已完成"),
    CLOSED(4,"已关闭"),
    INVALID(5,"无效订单");


    Integer code;
    String val;
    OrderStatusEnum(Integer code, String val){
        this.code=code;
        this.val=val;
    }
}
