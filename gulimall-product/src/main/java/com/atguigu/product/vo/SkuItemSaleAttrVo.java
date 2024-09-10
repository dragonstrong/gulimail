package com.atguigu.product.vo;
import lombok.Data;

import java.util.List;
/**
 * @Author qiang.long
 * @Date 2024/09/08
 * @Description 销售属性
 **/
@Data
public class SkuItemSaleAttrVo {
    /**
     * 属性id
     **/
    private Long attrId;
    /**
     * 属性名
     **/
    private String attrName;
    /**
     * 属性值： 比如颜色属性下有：黄色、黑色、蓝色，每种颜色里又有对应的信息（关联的sku id集合）
     **/

    private List<AttrValueWithSkuIdVo> attrValues;
}
