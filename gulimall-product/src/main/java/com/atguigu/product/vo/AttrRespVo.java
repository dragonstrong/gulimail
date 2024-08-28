package com.atguigu.product.vo;
import lombok.Data;

import java.util.List;
/**
 * @Author qiang.long
 * @Date 2024/08/28
 * @Description 响应体
 **/
@Data
public class AttrRespVo extends AttrVo{
    /**
     * 所属分类的名字
     **/
    private String catelogName;
    /**
     * 所属分组的名字
     **/
    private String groupName;

    /**
     * 分类完整路径
     **/
    private List<Long> catelogPath;



}
