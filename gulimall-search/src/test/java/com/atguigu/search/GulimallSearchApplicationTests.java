package com.atguigu.search;
import com.alibaba.fastjson.JSON;
import com.atguigu.search.config.ElasticSearchConfig;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
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




}
