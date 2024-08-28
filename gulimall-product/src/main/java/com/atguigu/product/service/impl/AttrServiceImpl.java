package com.atguigu.product.service.impl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.product.dao.AttrAttrgroupRelationDao;
import com.atguigu.product.dao.AttrDao;
import com.atguigu.product.dao.AttrGroupDao;
import com.atguigu.product.dao.CategoryDao;
import com.atguigu.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.product.entity.AttrEntity;
import com.atguigu.product.entity.AttrGroupEntity;
import com.atguigu.product.entity.CategoryEntity;
import com.atguigu.product.enumeration.AttrTypeEnum;
import com.atguigu.product.service.AttrAttrgroupRelationService;
import com.atguigu.product.service.AttrService;
import com.atguigu.product.service.CategoryService;
import com.atguigu.product.vo.AttrRespVo;
import com.atguigu.product.vo.AttrVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import jodd.bean.BeanUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {
    @Autowired
    AttrAttrgroupRelationService attrAttrgroupRelationService;
    @Autowired
    AttrDao attrDao;
    @Autowired
    CategoryDao categoryDao;
    @Autowired
    AttrGroupDao attrGroupDao;
    @Autowired
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;
    @Autowired
    CategoryService categoryService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }
    @Transactional
    @Override
    public void saveAttr(AttrVo attrVo) {
        AttrEntity attrEntity=new AttrEntity();
        BeanUtils.copyProperties(attrVo,attrEntity);
        attrDao.insertReturnId(attrEntity); // 自增主键插入数据（未携带attr_id）后返回主键
        AttrAttrgroupRelationEntity attrAttrgroupRelationEntity=new AttrAttrgroupRelationEntity();
        if(attrVo.getAttrType()==1){  // 销售属性无分组
            attrAttrgroupRelationEntity.setAttrGroupId(attrVo.getAttrGroupId()).setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationService.save(attrAttrgroupRelationEntity);
        }
    }
    @Override
    public PageUtils queryAttrPage(Long catelogId, String attrType,Map<String, Object> params) {
        QueryWrapper<AttrEntity> queryWrapper=new QueryWrapper<AttrEntity>().eq("attr_type","base".equals(attrType)?AttrTypeEnum.BASE_TYPE.getCode():AttrTypeEnum.SALE_TYPE.getCode());
        if(catelogId!=0){
            queryWrapper.eq("catelog_id",catelogId);
        }
        if(params.get("key")!=null){
            String key=(String)params.get("key");
            if(!key.equals("")){
                queryWrapper.and(obj->{
                    obj.eq("attr_id",key).or().like("attr_name",key);
                });
            }
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );

        PageUtils pageUtils=new PageUtils(page);
        List<AttrEntity> records=page.getRecords();
        List<AttrRespVo> respVos=records.stream().map(attrEntity -> {
            AttrRespVo attrRespVo=new AttrRespVo();
            BeanUtils.copyProperties(attrEntity,attrRespVo);
            // 设置分组和分类名字
            CategoryEntity categoryEntity=categoryDao.selectById(attrEntity.getCatelogId());
            if(categoryEntity!=null){
                attrRespVo.setCatelogName(categoryEntity.getName());
            }
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity=attrAttrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attrEntity.getAttrId()));
            if(attrAttrgroupRelationEntity!=null&&attrAttrgroupRelationEntity.getAttrGroupId()!=null){
                AttrGroupEntity attrGroupEntity=attrGroupDao.selectById(attrAttrgroupRelationEntity.getAttrGroupId());
                if(attrGroupEntity!=null){
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                    attrRespVo.setAttrGroupId(attrGroupEntity.getAttrGroupId());
                }
            }
            return attrRespVo;
        }).collect(Collectors.toList());
        pageUtils.setList(respVos); // 使用最新结果集合
        return pageUtils;
    }
    @Override
    public AttrRespVo getAttrInfo(Long attrId) throws Exception {
        AttrEntity attrEntity=getById(attrId);
        AttrRespVo attrRespVo=new AttrRespVo();
        // 设置分类完整路径
        BeanUtils.copyProperties(attrEntity,attrRespVo);
        List<Long> catelogPath=categoryService.finCatalogPath(attrEntity.getCatelogId());
        attrRespVo.setCatelogPath(catelogPath);
        // 设置分组信息
        if(attrEntity.getAttrType()==AttrTypeEnum.BASE_TYPE.getCode()){
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity=attrAttrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attrEntity.getAttrId()));
            if(attrAttrgroupRelationEntity!=null){
                attrRespVo.setAttrGroupId(attrAttrgroupRelationEntity.getAttrGroupId());
                AttrGroupEntity attrGroupEntity=attrGroupDao.selectById(attrAttrgroupRelationEntity.getAttrGroupId());
                if(attrGroupEntity!=null) {
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }
        return attrRespVo;
    }
    @Transactional
    @Override
    public void updateAttr(AttrVo attrVo) {
        AttrEntity attrEntity=new AttrEntity();
        BeanUtils.copyProperties(attrVo,attrEntity);
        updateById(attrEntity);
        // 修改分组关联(仅对基本属性)
        if(attrVo.getAttrType()==AttrTypeEnum.BASE_TYPE.getCode()){
            List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntityies=attrAttrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attrEntity.getAttrId()));
            AttrAttrgroupRelationEntity relation=new AttrAttrgroupRelationEntity();
            relation.setAttrId(attrVo.getAttrId()).setAttrGroupId(attrVo.getAttrGroupId());
            if(attrAttrgroupRelationEntityies!=null&&!attrAttrgroupRelationEntityies.isEmpty()){
                relation.setAttrGroupId(attrVo.getAttrGroupId());
                attrAttrgroupRelationDao.update(relation,new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attrEntity.getAttrId()));
            }else{
                attrAttrgroupRelationDao.insert(relation);
            }
        }
    }
    @Override
    public void deleteByIds(Long[] attrIds) {
        attrAttrgroupRelationDao.delete(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_id",attrIds));
        removeByIds(Arrays.asList(attrIds));
    }
}