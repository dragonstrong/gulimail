package com.atguigu.product.vo;
import lombok.Data;
/**
 * @Author qiang.long
 * @Date 2024/09/10
 * @Description  属性值关联的su id集合-  支持销售组合切换功能
 **/
@Data
public class AttrValueWithSkuIdVo {
    /**
     * 属性值
     **/
    private String attrValue;
    /**
     * 该属性值值关联的skuId集合
     **/
    private String skuIds;
}
