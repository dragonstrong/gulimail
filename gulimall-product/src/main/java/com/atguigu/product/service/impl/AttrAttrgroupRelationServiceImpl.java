package com.atguigu.product.service.impl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.product.dao.AttrAttrgroupRelationDao;
import com.atguigu.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.product.service.AttrAttrgroupRelationService;
import com.atguigu.product.vo.AttrAttrGroupRelationVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {
    @Autowired
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrAttrgroupRelationEntity> page = this.page(
                new Query<AttrAttrgroupRelationEntity>().getPage(params),
                new QueryWrapper<AttrAttrgroupRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void relation(AttrAttrGroupRelationVo[] attrAttrGroupRelationVos) {
        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities= Arrays.asList(attrAttrGroupRelationVos).stream().map(attrAttrGroupRelationVo -> {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity=new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(attrAttrGroupRelationVo,attrAttrgroupRelationEntity);
            return attrAttrgroupRelationEntity;
        }).collect(Collectors.toList());

        saveBatch(attrAttrgroupRelationEntities); // 批量保存
    }
    @Override
    public void delRelation(AttrAttrGroupRelationVo[] attrAttrGroupRelationVos) {
        attrAttrgroupRelationDao.deleteBatch(Arrays.asList(attrAttrGroupRelationVos)); // 批量删除
    }
}