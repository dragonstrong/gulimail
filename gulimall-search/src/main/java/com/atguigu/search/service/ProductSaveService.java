package com.atguigu.search.service;
import com.atguigu.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;
/**
 * @Author qiang.long
 * @Date 2024/09/02
 * @Description
 **/
public interface ProductSaveService {
    /**
     * @description: 商品上架：把数据存入ES
     * @param:
     * @param skuEsModels
     * @return: boolean 是否全部上架成功
     **/
    boolean productStartUp(List<SkuEsModel> skuEsModels) throws IOException;
}
