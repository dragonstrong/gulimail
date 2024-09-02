package com.atguigu.search.controller;
import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.Result;
import com.atguigu.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
/**
 * @Author qiang.long
 * @Date 2024/09/02
 * @Description
 **/
@Slf4j
@RequestMapping("/search")
@RestController
public class ElasticSaveController {
    @Autowired
    ProductSaveService productSaveService;
    /**
     * @description: 上架商品
     * @param:
     * @param skuEsModels
     * @return: com.atguigu.common.utils.Result
     **/
    @PostMapping("/product/up")
    public Result productStartUp(@RequestBody List<SkuEsModel> skuEsModels){
        try {
            productSaveService.productStartUp(skuEsModels);
        }catch (Exception e){
            log.error("ElasticSearch商品上架（保存数据）出现异常");
            return Result.error(BizCodeEnum.PRODUCT_UP_EXCEPTION);
        }
        return Result.ok();
    }

}
