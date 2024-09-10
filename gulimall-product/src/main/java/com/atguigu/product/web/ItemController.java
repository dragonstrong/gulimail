package com.atguigu.product.web;
import com.atguigu.product.service.SkuInfoService;
import com.atguigu.product.vo.SkuItemVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;
/**
 * @Author qiang.long
 * @Date 2024/09/08
 * @Description 商品详情页
 **/
@Slf4j
@Controller
public class ItemController {
    @Autowired
    SkuInfoService skuInfoService;
    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {
        log.info("查询商品详情,skuId={}",skuId);
        SkuItemVo skuItemVo=skuInfoService.item(skuId);
        model.addAttribute("item",skuItemVo);
        return "item";
    }

}
