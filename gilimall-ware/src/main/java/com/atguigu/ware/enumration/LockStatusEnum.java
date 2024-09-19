package com.atguigu.ware.enumration;
import lombok.Getter;
/**
 * @Author qiang.long
 * @Date 2024/09/18
 * @Description sku库存工作单详情锁定状态枚举
 **/
@Getter
public enum LockStatusEnum {
    LOCKED(1,"已锁定"),
    UNLOCKED(2,"已解锁"),
    DEDUCTED(3,"扣减");
    private Integer code;
    private String msg;
    LockStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
