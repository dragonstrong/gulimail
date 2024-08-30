package com.atguigu.product.service.impl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.product.dao.AttrAttrgroupRelationDao;
import com.atguigu.product.dao.AttrDao;
import com.atguigu.product.dao.AttrGroupDao;
import com.atguigu.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.product.entity.AttrEntity;
import com.atguigu.product.entity.AttrGroupEntity;
import com.atguigu.product.service.AttrGroupService;
import com.atguigu.product.vo.AttrgroupWithAttrs;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
@Slf4j
@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {
    @Autowired
    AttrAttrgroupRelationDao  attrAttrgroupRelationDao;
    @Autowired
    AttrDao attrDao;
    @Autowired
    AttrGroupDao attrGroupDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }
    @Override
    public PageUtils MyQueryPage(Map<String, Object> params, Long catelogId) {
        QueryWrapper<AttrGroupEntity> queryWrapper=new QueryWrapper<AttrGroupEntity>();
        if(catelogId!=0){ // 查询所有属性（数据库中没有catelogId为0的）
            queryWrapper.eq("catelog_id",catelogId);
        }
        if(params.get("key")!=null){
            String key=(String) params.get("key"); // 检索关键字
            if(!key.equals("")){
                queryWrapper.and((obj)->{
                    obj.eq("attr_group_id",key).or().like("attr_group_name",key);
                });
            }
        }
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params), // 分页条件
                queryWrapper // 查询条件
        );
        return new PageUtils(page);
    }
    @Override
    public List<AttrgroupWithAttrs> getGroupsAndAttrsByCatId(Long catelogId) {
        List<AttrgroupWithAttrs> attrGroupEntities=Optional.ofNullable(attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id",catelogId))).orElse(new ArrayList<>()).stream().map(attrGroupEntity -> {
            // 分组属性对拷
            AttrgroupWithAttrs attrgroupWithAttrs=new AttrgroupWithAttrs();
            BeanUtils.copyProperties(attrGroupEntity,attrgroupWithAttrs);
            // 查询该分组下所有属性
            List<Long> attrIds=Optional.ofNullable(attrAttrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id",attrgroupWithAttrs.getAttrGroupId()))).orElse(new ArrayList<>()).stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
            List<AttrEntity> attrEntities=attrDao.selectBatchIds(attrIds);
            attrgroupWithAttrs.setAttrs(attrEntities);
            return attrgroupWithAttrs;
        }).collect(Collectors.toList());
        return attrGroupEntities;
    }
}