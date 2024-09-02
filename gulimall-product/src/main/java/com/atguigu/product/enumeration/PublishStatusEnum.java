package com.atguigu.product.enumeration;
import lombok.Getter;
/**
 * @Author qiang.long
 * @Date 2024/09/02
 * @Description spu上架状态
 **/
@Getter
public enum PublishStatusEnum {
    NEW(0,"新建"),
    UP(1,"上架"),
    DOWN(2,"下架");
    private Integer code;
    private String msg;
    PublishStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
