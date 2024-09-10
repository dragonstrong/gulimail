package com.atguigu.product.vo;
import lombok.Data;

import java.util.List;
/**
 * @Author qiang.long
 * @Date 2024/09/08
 * @Description 属性分组（下含属性）
 **/
@Data
public class SpuItemAttrGroupVo {
    private String groupName;
    private List<SpuBaseAttrVo> attrs;
}
