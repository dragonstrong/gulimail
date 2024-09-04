package com.atguigu.search.service.impl;
import com.alibaba.fastjson.JSON;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.search.config.ElasticSearchConfig;
import com.atguigu.search.constant.EsContant;
import com.atguigu.search.service.MallSearchService;
import com.atguigu.search.vo.SearchParam;
import com.atguigu.search.vo.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
/**
 * @Author qiang.long
 * @Date 2024/09/04
 * @Description
 **/
@Slf4j
@Service
public class MallSearchServiceImpl implements MallSearchService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Override
    public SearchResult search(SearchParam searchParam) throws IOException {
        // 去ES中查询
        SearchResult searchResult=null;
        // 1.构造DSL查询条件 ，根据 gulimall-search/src/main/resources/DSL.json
        SearchRequest searchRequest=buildSearchRequest(searchParam);
        try{
            // 2.执行检索请求
            SearchResponse searchResponse=restHighLevelClient.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);
            // 3. 分析响应数据并封装成所需格式
            searchResult=buildSearchResult(searchResponse,searchParam);
        }catch (Exception e){
            log.error("从ES中检索数据发生异常,searchParam:{},Exception:{}",JSON.toJSONString(searchParam),JSON.toJSONString(e));
        }
        return searchResult;
    }

    /**
     * @description: 构建检索请求
     **/
    private SearchRequest buildSearchRequest(SearchParam searchParam) {
        /**
         * 1.构建DSL语句
         **/
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        /**
         *  1.1 模糊匹配，过滤（按属性、分类、品牌、价格区间、库存）
         **/

        // 1.1.1 构建bool-query
        // must-模糊匹配
        BoolQueryBuilder boolQueryBuilder= QueryBuilders.boolQuery();
        if(searchParam!=null&&!StringUtils.isEmpty(searchParam.getKeyword())){
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle",searchParam.getKeyword()));
        }

        // 1.1.2 bool -filter  按三级分类ID查
        if(searchParam.getCatalog3Id()!=null){
            boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId",searchParam.getCatalog3Id()));
        }

        // 1.1.2 bool -filter  按品牌ID列表查
        if(searchParam.getBrandId()!=null&&!searchParam.getBrandId().isEmpty()){
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId",searchParam.getBrandId()));
        }

        // 1.1.3 bool -filter  按所有指定的属性查
        if(searchParam.getAttrs()!=null&&!searchParam.getAttrs().isEmpty()){
            // attrs=1_5寸:8寸&attrs=2_16G:8G
            for (String attrStr:searchParam.getAttrs()){
                BoolQueryBuilder nestboolQueryBuilder=QueryBuilders.boolQuery();
                String[] s=attrStr.split("_");
                String attrId=s[0]; //检索用的属性Id
                String[] attrValue=s[1].split(":"); // 该属性用的值
                nestboolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrId",attrId));
                nestboolQueryBuilder.must(QueryBuilders.termsQuery("attrs.attrValue",attrValue));
                // 每个属性都要加入外部大boolQueryBuilder
                //嵌入式Query  ScoreMode.None不参与评分
                NestedQueryBuilder nestedQueryBuilder =QueryBuilders.nestedQuery("attrs",nestboolQueryBuilder, ScoreMode.None);
                boolQueryBuilder.filter(nestedQueryBuilder);
            }

        }

        // 1.1.4 bool -filter  按是否有库存查
        if(searchParam.getHasStock()!=null){
            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock",searchParam.getHasStock()==1));
        }

        // 1.1.5 bool -filter  按价格区间查
        if(searchParam.getSkuPrice()!=null&&!StringUtils.isEmpty(searchParam.getSkuPrice())){
            /** 1_500/_500/500_
             * "range": {
             *             "skuPrice": {
             *               "gte": 5000,
             *               "lte": 8000
             *             }
             *           }
             **/
            RangeQueryBuilder rangeQueryBuilder= QueryBuilders.rangeQuery("skuPrice");
            String[] s=searchParam.getSkuPrice().split("_");
            if(searchParam.getSkuPrice().startsWith("_")){ // 小于等于
                rangeQueryBuilder.lte(s[0]);
            }else if(searchParam.getSkuPrice().endsWith("_")){  // 大于等于
                rangeQueryBuilder.gte(s[0]);
            }else{ // 区间
                rangeQueryBuilder.gte(s[0]).lte(s[1]);
            }
            boolQueryBuilder.filter(rangeQueryBuilder);
        }

        // 整个大boolQueryBuilder放入searchSourceBuilder
        searchSourceBuilder.query(boolQueryBuilder);

        /**
         * 1.2 排序 分页 高亮
         **/

        /**
         * 1.2.1 排序
         *
         *      * 排序条件
         *      * sort=saleCount_asc/desc  销量
         *      * sort=skuPrice_asc/desc   价格
         *      * sort=hotScore_asc/desc   热度
         *
         **/
        if(searchParam.getSort()!=null&&!StringUtils.isEmpty(searchParam.getSort())){
            String sort=searchParam.getSort();
            String[] s=sort.split("_");
            SortOrder order=s[1].equalsIgnoreCase("asc")?SortOrder.ASC:SortOrder.DESC;
            searchSourceBuilder.sort(s[0], order);
        }

        /**
         * 1.2.2 分页
         * pageNum:1  from=0 pageSize:5  [0,1,2,3,4]
         * pageNum:2  from=5 pageSize:5  [5,6,7,8,9]
         **/
        searchSourceBuilder.from((searchParam.getPageNum()-1)*EsContant.PRODUCT_PAGE_SIZE);
        searchSourceBuilder.size(EsContant.PRODUCT_PAGE_SIZE);

        /**1.2.3 高亮
         **/
        if(searchParam.getKeyword()!=null&&!StringUtils.isEmpty(searchParam.getKeyword())){
            HighlightBuilder highlightBuilder=new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            searchSourceBuilder.highlighter(highlightBuilder);

        }


        /**
         * 1.3 聚合分析
         **/
        // 1.3.1 品牌聚合
        TermsAggregationBuilder brandAgg= AggregationBuilders.terms("brand_agg").field("brandId").size(50);
        // 品牌聚合的子聚合
        brandAgg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brandAgg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));
        searchSourceBuilder.aggregation(brandAgg);

        // 1.3.2 分类聚合
        TermsAggregationBuilder catalogAgg= AggregationBuilders.terms("catalog_agg").field("catalogId").size(20);
        // 分类聚合的子聚合
        catalogAgg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1));
        searchSourceBuilder.aggregation(catalogAgg);

        // 1.3.3 属性聚合
        NestedAggregationBuilder attrAgg= AggregationBuilders.nested("attr_agg","attrs");
        TermsAggregationBuilder attrIdAgg=AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        attrAgg.subAggregation(attrIdAgg);
        searchSourceBuilder.aggregation(attrAgg);
        // 测试：验证代码正确定，放到kibina中执行
        log.info("构建的DSL：{}",searchSourceBuilder);
        SearchRequest searchRequest=new SearchRequest(new String[]{EsContant.PRODUCT_INDEX},searchSourceBuilder);
        return searchRequest;
    }

    /**
     * @description: 封装结果数据
     * @param:
     * @param searchResponse
     * @return: com.atguigu.search.vo.SearchResult
     **/
    private SearchResult buildSearchResult(SearchResponse searchResponse,SearchParam searchParam) {
        SearchResult result=new SearchResult();
        /**
         * 1. 所有查询到的商品信息
         **/
        SearchHits hits=searchResponse.getHits(); //获取查询命中的所有数据
        List<SkuEsModel> skuEsModels=new ArrayList<>();
        if(hits!=null&&hits.getHits().length>0){
            for(SearchHit hit: hits.getHits()){
                String sourceAsString=hit.getSourceAsString();
                SkuEsModel skuEsModel= JSON.parseObject(sourceAsString,SkuEsModel.class);
                // 设置高亮
                if(searchParam.getKeyword()!=null&&!StringUtils.isEmpty(searchParam.getKeyword())){
                    HighlightField skuTitle=hit.getHighlightFields().get("skuTitle");
                    String  s=skuTitle.getFragments()[0].string();
                    skuEsModel.setSkuTitle(s);
                }
                skuEsModels.add(skuEsModel);
            }
        }
        result.setProducts(skuEsModels);
        /**
         * 2. 所有查询到的商品涉及的属性信息
         **/
        List<SearchResult.AttrVo> attrVos=new ArrayList<>();
        ParsedNested attAgg=searchResponse.getAggregations().get("attr_agg");
        ParsedLongTerms attrIdAgg=attAgg.getAggregations().get("attr_id_agg");
        for(Terms.Bucket bucket:attrIdAgg.getBuckets()){
            SearchResult.AttrVo  attrVo=new SearchResult.AttrVo();
            // 属性Id
            long attrId=bucket.getKeyAsNumber().longValue();
            // 属性名
            String attrName=((ParsedStringTerms)bucket.getAggregations().get("attr_name_agg")).getBuckets().get(0).getKeyAsString();
            // 属性值
            List<String> attrValues=((ParsedStringTerms)bucket.getAggregations().get("attr_value_agg")).getBuckets().stream().map(item -> {
                return ((Terms.Bucket)item).getKeyAsString();
            }).collect(Collectors.toList());
            attrVo.setAttrId(attrId);
            attrVo.setAttrName(attrName);
            attrVo.setAttrValue(attrValues);
            attrVos.add(attrVo);
        }
        result.setAttrs(attrVos);


        /**
         * 3. 所有涉及到的品牌信息
         **/
        ParsedLongTerms brandAgg=searchResponse.getAggregations().get("brand_agg");
        List<SearchResult.BrandVo> brandVos=new ArrayList<>();
        List<? extends Terms.Bucket> brandBuckets=brandAgg.getBuckets();
        for(Terms.Bucket bucket: brandBuckets){
            SearchResult.BrandVo brandVo=new SearchResult.BrandVo();
            // 品牌ID
            String keyAsString=bucket.getKeyAsString();
            brandVo.setBrandId(Long.parseLong(keyAsString));
            // 拿到品牌名
            ParsedStringTerms brandNameAgg=bucket.getAggregations().get("brand_name_agg");
            brandVo.setBrandName(brandNameAgg.getBuckets().get(0).getKeyAsString());
            // 拿到品牌图片
            ParsedStringTerms brandImgAgg=bucket.getAggregations().get("brand_img_agg");
            brandVo.setBrandImg(brandImgAgg.getBuckets().get(0).getKeyAsString());
            brandVos.add(brandVo);
        }
        result.setBrands(brandVos);

        /**
         * 4. 所有涉及到的分类信息
         **/
        ParsedLongTerms catalogAgg=searchResponse.getAggregations().get("catalog_agg");
        List<SearchResult.CatalogVo> catalogVos=new ArrayList<>();
        List<? extends Terms.Bucket> catalogBuckets=catalogAgg.getBuckets();
        for(Terms.Bucket bucket: catalogBuckets){
            SearchResult.CatalogVo catalogVo=new SearchResult.CatalogVo();
            // 分类ID
            String keyAsString=bucket.getKeyAsString();
            catalogVo.setCatalogId(Long.parseLong(keyAsString));
            // 拿到分类名
            ParsedStringTerms catalogNameAgg=bucket.getAggregations().get("catalog_name_agg");
            catalogVo.setCatalogName(catalogNameAgg.getBuckets().get(0).getKeyAsString());
            catalogVos.add(catalogVo);
        }
        result.setCatalogs(catalogVos);

        /**
         * 5.分页信息-页码
         **/
        result.setPageNum(searchParam.getPageNum()); // 当前页码
        long total=hits.getTotalHits().value;
        result.setTotal(total); // 总记录数
        long totalPages=total%EsContant.PRODUCT_PAGE_SIZE==0? total/EsContant.PRODUCT_PAGE_SIZE:(total/EsContant.PRODUCT_PAGE_SIZE+1);
        result.setTotalPages((int)totalPages); // 总分页数
        List<Integer> pageNavs=new ArrayList<>();
        for(int i=1;i<=totalPages;i++){
            pageNavs.add(i);
        }
        result.setPageNavs(pageNavs);   //导航页
        return result;
    }
}
