package com.atguigu.search.vo;
import lombok.Data;

import java.util.List;
/**
 * @Author qiang.long
 * @Date 2024/09/04
 * @Description 检索参数 （前端传来）
 *
 *
 **/
@Data
public class SearchParam {
    /**
     * 检索关键字
     **/
    private String keyword;
    /**
     * 三级分类Id
     **/
    private Long catalog3Id;
    /**
     * 排序条件
     * sort=saleCount_asc/desc  销量
     * sort=skuPrice_asc/desc   价格
     * sort=hotScore_asc/desc   热度
     **/
    private String sort;

    /**
     * 其他过滤条件
     * hasStock(是否有货)  skuPrice区间  brandId、catalog3Id、attrs
     * hasStock=0/1
     * skuPrice区间: 1_500/_500/500_
     **/
    /**
     * 是否有货：0无  1有
     **/
    private Integer hasStock;
    /**
     * 商品价格区间
     **/
    private String skuPrice;
    /**
     * 品牌Id :可同时选多个
     **/
    private List<String> brandId;
    /**
     * 属性 :可同时选多个
     **/
    private List<String> attrs;

    /**
     * 分页当前页码(起始号)，默认1
     **/
    private Integer pageNum=1;





}
