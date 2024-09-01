package com.atguigu.product.controller;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;
import com.atguigu.product.entity.ProductAttrValueEntity;
import com.atguigu.product.service.AttrService;
import com.atguigu.product.service.ProductAttrValueService;
import com.atguigu.product.vo.AttrRespVo;
import com.atguigu.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;



/**
 * 商品属性相关接口（基本属性+销售属性）
 * @author qiang.long
 * @date 2024-03-18 11:32:43
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;
    @Autowired
    private ProductAttrValueService  productAttrValueService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }
    /**
     * @description: 获取属性，比如获取手机该三级分类下的所有(基本或销售)属性
     * @param:
     * @param catelogId 属性所属分类Id
     * @param attrType 基本属性还是销售属性：base/sale
     * @param params 分页参数
     * @return: List<AttrEntity>
     **/
    @GetMapping("/{attrType}/list/{catelogId}")
    public R getBaseAttr(@PathVariable("catelogId") Long catelogId, @PathVariable("attrType") String attrType,@RequestParam Map<String, Object> params){
        PageUtils page=attrService.queryAttrPage(catelogId,attrType,params);
        return R.ok().put("page",page);
    }

    /**
     * @description: 获取Spu基本属性
     * @param:
     * @param spuId
     * @return: com.atguigu.common.utils.R
     **/
    @GetMapping("/base/listforspu/{spuId}")
    public R getBaseAttrForSpu(@PathVariable("spuId") Long spuId){
        List<ProductAttrValueEntity> productAttrValueEntityList=productAttrValueService.baseAttrlistForSpu(spuId);
        return R.ok().put("data",productAttrValueEntityList);
    }

    @PostMapping("/update/{spuId}")
    public R updateBaseAttrForSpu(@PathVariable("spuId") Long spuId,@RequestBody List<ProductAttrValueEntity> productAttrValueEntities){
        productAttrValueService.updateBaseAttrForSpu(spuId,productAttrValueEntities);
        return R.ok();
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    //@RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId) throws Exception {
        AttrRespVo attrRespVo = attrService.getAttrInfo(attrId);
        return R.ok().put("attr", attrRespVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attrVo){
		attrService.saveAttr(attrVo);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrVo attrVo){
		attrService.updateAttr(attrVo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.deleteByIds(attrIds);
        return R.ok();
    }

}
