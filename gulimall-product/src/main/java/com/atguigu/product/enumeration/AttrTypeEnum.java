package com.atguigu.product.enumeration;
import lombok.Getter;
/**
 * @Author qiang.long
 * @Date 2024/08/29
 * @Description 属性分类枚举
 **/
@Getter
public enum AttrTypeEnum {
    SALE_TYPE(0,"销售属性"),
    BASE_TYPE(1,"基本属性");
    private Integer code;
    private String msg;
    AttrTypeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
