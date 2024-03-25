package com.atguigu.product.controller;
import com.atguigu.product.service.CategoryService;
import com.atguigu.product.vo.Catalog2Vo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
/**
 * @Author qiang.long
 * @Date 2024/03/25
 * @Description
 **/

@RestController
public class IndexController {

    @Resource
    private CategoryService categoryService;
    /**
     * @description: p138 获取二级三级分类数据
     * @param:
     * @return: Map<String,List<Catalog2Vo>>
     **/
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catalog2Vo>> getCatalogJson(){
        //return categoryService.getCatalogJson();
        return categoryService.getCatalogJsonUseRedis();
    }
}
