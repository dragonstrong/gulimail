package com.atguigu.product.service.impl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.product.dao.CategoryDao;
import com.atguigu.product.entity.CategoryEntity;
import com.atguigu.product.service.CategoryService;
import com.atguigu.product.vo.Catalog2Vo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }
    /**
     * @description: 查出所有分类以及子分类，并以树形结构组装起来
     * @param:
     * @return: List<CategoryEntity>
     **/
    @Override
    public List<CategoryEntity> getCategoryTree(){
        List<CategoryEntity> categoryEntityList=list();
        // 找出1级分类并设置子菜单
        List<CategoryEntity> res=categoryEntityList.stream().filter(categoryEntity -> categoryEntity.getParentCid()==0).map(category -> {
            category.setChildren(getChildren(category,categoryEntityList));
            return category;
        }).sorted((category1,category2)->{
            return (category1.getSort()==null?0:category1.getSort())-(category2.getSort()==null?0:category2.getSort());
        }).collect(Collectors.toList());
        return res;
    }


    /**
     * @description: 递归找子菜单
     * @param:
     * @param category 当前菜单
     * @param allCategoryEntity  所菜单
     * @return: List<CategoryEntity>
     **/

    public List<CategoryEntity> getChildren(CategoryEntity category, List<CategoryEntity> allCategoryEntity){
        List<CategoryEntity> child=allCategoryEntity.stream().filter(category1 -> category1.getParentCid()==category.getCatId()).map(category2 ->{
            category2.setChildren(getChildren(category2,allCategoryEntity));
            return category2;
        } ).sorted((muen1,muen2)->{
            return (muen1.getSort()==null?0:muen1.getSort())-(muen2.getSort()==null?0:muen2.getSort());
        }).collect(Collectors.toList());
        category.setChildren(child);
        return child;
    }

    /**
     * @description: p138 二级三级分类数据
     * @param:
     * @return: List<Catalog2Vo>>
     **/
    @Override
    public Map<String, List<Catalog2Vo>> getCatalogJson(){
        Map<String, List<Catalog2Vo>> map=null;  // 结果
        // 找一级分类
        List<CategoryEntity> categoryEntityList=getBaseMapper().selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid",0));
        if (categoryEntityList!=null){
            // 对每一个一级分类找二级分类
            // 封装成map   key=一级分类的catId  value=List<Catalog2Vo>
            map=categoryEntityList.stream().collect(Collectors.toMap(k->{
                return k.getCatId().toString();
            },v->{
                // 找v的二级分类列表
                List<Catalog2Vo> catalog2VoList=null;
                List<CategoryEntity> category2Entities=getBaseMapper().selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid",v.getCatId()));
                if (category2Entities!=null){
                    // 将category2Entities封装成List<Catalog2Vo>
                    // 先找3级子分类列表
                        catalog2VoList=category2Entities.stream().map(l2->{
                        List<CategoryEntity> category3Entities=getBaseMapper().selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid",l2.getCatId()));
                        List<Catalog2Vo.Category3Vo> category3VoList=null;
                        if (category3Entities!=null){
                            category3VoList=category3Entities.stream().map(l3->{
                                Catalog2Vo.Category3Vo catalog3Vo=new Catalog2Vo.Category3Vo(l2.getCatId().toString(),l3.getCatId().toString(),l3.getName());
                                return catalog3Vo;
                            }).collect(Collectors.toList());
                        }
                        Catalog2Vo catalog2Vo=new Catalog2Vo(l2.getParentCid().toString(),category3VoList,l2.getCatId().toString(),l2.getName());
                        return catalog2Vo;
                    }).collect(Collectors.toList());
                }
                return catalog2VoList;
            }));
        }
        return map;
    }
}