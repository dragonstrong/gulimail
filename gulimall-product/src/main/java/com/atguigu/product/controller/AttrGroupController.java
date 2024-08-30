package com.atguigu.product.controller;
import com.alibaba.fastjson.JSON;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;
import com.atguigu.product.entity.AttrEntity;
import com.atguigu.product.entity.AttrGroupEntity;
import com.atguigu.product.service.AttrAttrgroupRelationService;
import com.atguigu.product.service.AttrGroupService;
import com.atguigu.product.service.AttrService;
import com.atguigu.product.service.CategoryService;
import com.atguigu.product.vo.AttrAttrGroupRelationVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;



/**
 * 属性分组
 *
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-18 11:32:43
 */
@Slf4j
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    AttrAttrgroupRelationService attrAttrgroupRelationService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    AttrService attrService;

    /**
     * 获取分类属性分组
     */
    @RequestMapping("/list/{catelogId}")
    //@RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params,@PathVariable("catelogId") Long catelogId){
        log.info("获取分类属性分组：params={},catelogId={}",JSON.toJSON(params),catelogId);
        PageUtils page = attrGroupService.MyQueryPage(params,catelogId);
        return R.ok().put("page", page);
    }


    /**
     * 获取属性分组详情
     */
    @RequestMapping("/info/{attrGroupId}")
    //@RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId) throws Exception {
        log.info("获取属性分组详情：attrGroupId={}",attrGroupId);
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        attrGroup.setCatelogPath(categoryService.finCatalogPath(attrGroup.getCatelogId()));

        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));
        return R.ok();
    }

    /**
     * 获取当前分组下的所有基本属性
     */
    @GetMapping("/{attrgroupId}/attr/relation")
    public R getRelationAttrs(@PathVariable("attrgroupId") Long attrgroupId){
        List<AttrEntity> attrs=attrService.getRelationAttrs(attrgroupId);
        return R.ok().put("data",attrs);
    }

    /**
     * @description: 获取属性分组没有关联的其他属性
     * @param:
     * @param attrgroupId 分组
     * @param params 分页参数
     * @return: com.atguigu.common.utils.R
     **/
    @GetMapping("/{attrgroupId}/noattr/relation")
    public R getNoRelationAttrs(@PathVariable("attrgroupId") Long attrgroupId,@RequestParam Map<String, Object> params) throws Exception {
        PageUtils pageUtils=attrService.getNoRelationAttrs(attrgroupId,params);
        return R.ok().put("page",pageUtils);
    }

    /**
     * 关联属性-分组
     */
    @PostMapping("/attr/relation")
    public R relation(@RequestBody AttrAttrGroupRelationVo[] attrAttrGroupRelationVos){
        attrAttrgroupRelationService.relation(attrAttrGroupRelationVos);
        return R.ok();
    }
    /**
     * 删除属性-分组
     */
    @PostMapping ("/attr/relation/delete")
    public R delRelation(@RequestBody AttrAttrGroupRelationVo[] attrAttrGroupRelationVos){
        attrAttrgroupRelationService.delRelation(attrAttrGroupRelationVos);
        return R.ok();
    }

}
