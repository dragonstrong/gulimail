package com.atguigu.ware.enumration;
import lombok.Getter;
/**
 * @Author qiang.long
 * @Date 2024/08/31
 * @Description 采购单状态
 **/
@Getter
public enum PurchaseStatusEnum {
    CREATED(0,"新建"),
    ASSIGNED(1,"已分配"),
    RECEIVE(2,"已领取"),
    FINISH(3,"已完成"),
    ERROR(4,"异常");
    private Integer code;
    private String msg;
    PurchaseStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
