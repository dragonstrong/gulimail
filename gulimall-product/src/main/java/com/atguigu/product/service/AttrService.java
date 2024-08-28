package com.atguigu.product.service;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.product.entity.AttrEntity;
import com.atguigu.product.vo.AttrRespVo;
import com.atguigu.product.vo.AttrVo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 商品属性
 *
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-17 23:41:04
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);
    void saveAttr(AttrVo attrVo);
    PageUtils queryAttrPage(Long catelogId, String attrType,Map<String, Object> params);
    AttrRespVo getAttrInfo(Long attrId) throws Exception;
    void updateAttr(AttrVo attrVo);
    void deleteByIds(Long[] attrIds);
}

