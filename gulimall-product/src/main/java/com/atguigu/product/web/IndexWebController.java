package com.atguigu.product.web;
import com.atguigu.product.entity.CategoryEntity;
import com.atguigu.product.service.CategoryService;
import com.atguigu.product.vo.Catalog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
/**
 * @Author qiang.long
 * @Date 2024/09/02
 * @Description 页面跳转
 **/

@Controller
public class IndexWebController {
    @Autowired
    CategoryService categoryService;
    /**
     * @description: 配置转发： 访问http://localhost:10000/index.html 和http://localhost:10000都跳转到index.html页面
     * @param:
     * @param model
     * @return: java.lang.String
     **/
    @GetMapping({"/","/index.html"})
    public String indexPage(Model model){
        // 1.查出所有的一级分类
        List<CategoryEntity> categoryEntities=categoryService.getLevelOneCategories();
        // 视图解析器
        model.addAttribute("categorys",categoryEntities);
        return "index";
    }
    /**
     * @description: 前端实时渲染二三级分类数据  前端对应static/index/js/catalogLoader.js
     * @param:
     * @return: Map<String,List<Catalog2Vo>>
     **/
    @ResponseBody
    @GetMapping("index/json/catalog.json")
    public Map<String, List<Catalog2Vo>> getCatalogJson(){
        return categoryService.getCatalogJsonByDB();
    }
}