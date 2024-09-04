package com.atguigu.search;
import com.alibaba.fastjson.JSON;
import com.atguigu.search.config.ElasticSearchConfig;
import com.atguigu.search.constant.EsContant;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
@Slf4j
@SpringBootTest(classes = GulimallSearchApplication.class)
public class GulimallSearchApplicationTests {
    @Autowired
    RestHighLevelClient restHighLevelClient;
    /**
     * @description: 测试存储数据到ES
     **/
    @Test
    public void indexData() throws IOException {
        IndexRequest indexRequest=new IndexRequest("user");
        indexRequest.id("2");  // 数据所在 id
        /**
         *方式1
         indexRequest.source("userName","zhangsan","age",18,"gender","男");  // K-V键值对
         **/

        /**
         *方式2： 直接传对象json字符串
         **/
        User user=new User();
        user.setUserName("zhangsan").setAge(18).setGender("男");
        indexRequest.source(JSON.toJSONString(user), XContentType.JSON);
        IndexResponse indexResponse=restHighLevelClient.index(indexRequest, ElasticSearchConfig.COMMON_OPTIONS);
        log.info("响应数据:{}",JSON.toJSONString(indexResponse));
    }

    @Accessors(chain = true)
    @Data
    public static class User{
        private String userName;
        private Integer age;
        private String gender;
    }
    /**
     * @description: 测试检索数据
     **/
    @Test
    public void searchData() throws IOException {
        // 1.创建检索请求
        SearchRequest searchRequest = new SearchRequest();
        // 指定索引
        searchRequest.indices("bank");
        // 指定DSL，检索条件
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        // 1.1 构造检索条件
//        searchSourceBuilder.query();
//        searchSourceBuilder.from();
//        searchSourceBuilder.size();
//        searchSourceBuilder.aggregation();
        searchSourceBuilder.query(QueryBuilders.matchQuery("address","mill"));
        log.info("检索条件:{}",searchSourceBuilder);
        searchRequest.source(searchSourceBuilder);
        // 2.执行检索
        SearchResponse searchResponse=restHighLevelClient.search(searchRequest,ElasticSearchConfig.COMMON_OPTIONS);

        // 3.分析检索结果
        log.info("检索结果：{}",searchResponse);


    }
    /**
     * @description: 指定映射
     **/
    //@Test
    public void createMapping() throws IOException {
        // 1.给es中建立索引: product,并建立映射关系 gulimall-search/src/main/resources/product-mapping.txt
        // 注意CreateIndexRequest导包，需为org.elasticsearch.client.indices.CreateIndexRequest;否则报错
        CreateIndexRequest request = new CreateIndexRequest(EsContant.PRODUCT_INDEX);
        request.mapping("{\n" +
                "    \"properties\": {\n" +
                "      \"skuId\": {\n" +
                "        \"type\": \"long\"\n" +
                "      },\n" +
                "      \"spuId\": {\n" +
                "        \"type\": \"keyword\"\n" +
                "      },\n" +
                "      \"skuTitle\": {\n" +
                "        \"type\": \"text\",\n" +
                "        \"analyzer\": \"ik_smart\"\n" +
                "      },\n" +
                "      \"skuPrice\": {\n" +
                "        \"type\": \"keyword\"\n" +
                "      },\n" +
                "      \"skuImg\": {\n" +
                "        \"type\": \"keyword\",\n" +
                "        \"index\": false,\n" +
                "        \"doc_values\": false\n" +
                "      },\n" +
                "      \"saleCount\": {\n" +
                "        \"type\": \"long\"\n" +
                "      },\n" +
                "      \"hasStock\": {\n" +
                "        \"type\": \"boolean\"\n" +
                "      },\n" +
                "      \"hotScore\": {\n" +
                "        \"type\": \"long\"\n" +
                "      },\n" +
                "      \"brandId\": {\n" +
                "        \"type\": \"long\"\n" +
                "      },\n" +
                "      \"catalogId\": {\n" +
                "        \"type\": \"long\"\n" +
                "      },\n" +
                "      \"brandName\": {\n" +
                "        \"type\": \"keyword\",\n" +
                "        \"index\": false,\n" +
                "        \"doc_values\": false\n" +
                "      },\n" +
                "      \"brandImg\": {\n" +
                "        \"type\": \"keyword\",\n" +
                "        \"index\": false,\n" +
                "        \"doc_values\": false\n" +
                "      },\n" +
                "      \"catalogName\": {\n" +
                "        \"type\": \"keyword\",\n" +
                "        \"index\": false,\n" +
                "        \"doc_values\": false\n" +
                "      },\n" +
                "      \"attrs\": {\n" +
                "        \"type\": \"nested\",\n" +
                "        \"properties\": {\n" +
                "          \"attrId\": {\n" +
                "            \"type\": \"long\"\n" +
                "          },\n" +
                "          \"attrName\": {\n" +
                "            \"type\": \"keyword\",\n" +
                "            \"index\": false,\n" +
                "            \"doc_values\": false\n" +
                "          },\n" +
                "          \"attrValue\": {\n" +
                "            \"type\": \"keyword\"\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }",XContentType.JSON);
        restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
    }




}
