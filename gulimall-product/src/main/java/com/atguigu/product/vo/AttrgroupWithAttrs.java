package com.atguigu.product.vo;
import com.atguigu.product.entity.AttrEntity;
import lombok.Data;

import java.util.List;
/**
 * @Author qiang.long
 * @Date 2024/08/30
 * @Description
 **/
@Data
public class AttrgroupWithAttrs {
    /**
     * 分组id
     */
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;
    /**
     * 该分组下所有属性
     */
    private List<AttrEntity> attrs;
}
