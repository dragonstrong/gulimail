package com.atguigu.search.vo;
import com.atguigu.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.List;
/**
 * @Author qiang.long
 * @Date 2024/09/04
 * @Description 检索结果（从ES中）
 **/
@Data
public class SearchResult {
    /**
     * sku在ES中的数据模型（商品信息）
     **/
    private List<SkuEsModel> products;

    /**
     * 下面为分页信息
     **/

    /**
     * 当前页码
     **/
    private Integer pageNum;
    /**
     * 总记录数
     **/
    private Long total;
    /**
     * 总页码
     **/
    private Integer totalPages;
    /**
     * 导航页码
     **/
    private List<Integer> pageNavs;


    /**
     * 当前检索结果涉及到的所有品牌信息
     **/
    private List<BrandVo> brands;
    /**
     * 当前检索结果涉及到的所有分类信息
     **/
    private List<CatalogVo> catalogs;

    /**
     * 当前检索结果涉及到的所有属性信息
     **/
    private List<AttrVo> attrs;

    //================以上是返给页面的所有信息===================

    // 面包屑导航数据
    private List<NavVo> navs;

    @Data
    public static class NavVo{
        /**
         * 导航名字
         **/
        private String navName;
        /**
         * 导航值
         **/
        private String navValue;
        /**
         * 取消条件跳到的地址
         **/
        private String link;
    }



    /**
     * 品牌信息
     **/
    @Data
    public static class BrandVo{
        /**
         * 品牌Id
         **/
        private Long brandId;
        /**
         * 品牌名
         **/
        private String brandName;
        /**
         * 品牌图片
         **/
        private String brandImg;
    }

    /**
     * 分类信息
     **/
    @Data
    public static class CatalogVo{
        /**
         * 分类Id
         **/
        private Long catalogId;
        /**
         * 分类名
         **/
        private String catalogName;
    }

    /**
     * 属性信息
     **/
    @Data
    public static class AttrVo{
        /**
         * 属性id
         */
        private Long attrId;
        /**
         * 属性名
         */
        private String attrName;
        /**
         * 属性值列表
         */
        private List<String> attrValue;
    }



}
