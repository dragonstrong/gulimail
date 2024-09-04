package com.atguigu.search.service;
import com.atguigu.search.vo.SearchParam;
import com.atguigu.search.vo.SearchResult;

import java.io.IOException;
/**
 * @Author qiang.long
 * @Date 2024/09/04
 * @Description 检索
 **/
public interface MallSearchService {
    /**
     * @description: 去ES中检索
     * @param:
     * @param searchParam 检索条件
     * @return: com.atguigu.search.vo.SearchResult 页面需要的检索结果
     **/
    SearchResult search(SearchParam searchParam) throws IOException;
}
