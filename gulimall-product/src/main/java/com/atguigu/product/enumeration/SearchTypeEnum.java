package com.atguigu.product.enumeration;
import lombok.Getter;
/**
 * @Author qiang.long
 * @Date 2024/09/02
 * @Description 是否需要被检索
 **/
@Getter
public enum SearchTypeEnum {
    NO(0,"无需检索"),
    YES(1,"需要检索");
    private Integer code;
    private String msg;
    SearchTypeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
