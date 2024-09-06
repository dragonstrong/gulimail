package com.atguigu.search.web;
import com.atguigu.search.service.MallSearchService;
import com.atguigu.search.vo.SearchParam;
import com.atguigu.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
/**
 * @Author qiang.long
 * @Date 2024/09/04
 * @Description 检索
 **/
@Controller
public class SearchController {


    @Autowired
    MallSearchService mallSearchService;

    /**
     * @description: 检索+  页面跳转：浏览器访问http://search.gulimall.com 和 http://search.gulimall.com/list.html  -> 都跳到list.html
     * @param:
     * @param searchParam 前端传来的检索条件
     * @return: String
     **/
    @GetMapping({"/","/list.html"})
    public String listPage(SearchParam searchParam, Model model) throws IOException {
        /**
         * 根据页面传来的检索参数去ES中检索，并返回结果
         **/
        SearchResult searchResult=mallSearchService.search(searchParam);
        // spring mvc会自动拼接上默认前缀  和 默认后缀
        model.addAttribute("result",searchResult); //  一定得和前端（list.html）对上，它取的变量名就叫result
        return "list";
    }

}
