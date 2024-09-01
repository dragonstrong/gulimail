package com.atguigu.ware.enumration;
import lombok.Getter;
/**
 * @Author qiang.long
 * @Date 2024/09/01
 * @Description 采购单详情状态枚举
 **/

@Getter
public enum PurchaseDetailStatusEnum {
    CREATED(0,"新建"),
    ASSIGNED(1,"已分配"),
    BUYING(2,"正在采购"),
    FINISH(3,"已完成"),
    FAIL(4,"采购失败");
    private Integer code;
    private String msg;
    PurchaseDetailStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}