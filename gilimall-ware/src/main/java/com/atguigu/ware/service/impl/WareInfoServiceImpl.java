package com.atguigu.ware.service.impl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.ware.dao.WareInfoDao;
import com.atguigu.ware.entity.WareInfoEntity;
import com.atguigu.ware.service.WareInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareInfoEntity> queryWrapper=new QueryWrapper<WareInfoEntity>();
        if(params.get("key")!=null){
            String key=(String)params.get("key");
            if(!StringUtils.isEmpty(key)){
                queryWrapper.and(obj->{
                    obj.eq("id",key).or().like("name",key).or().like("address",key)
                            .or().eq("areacode",key);
                });
            }
        }
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

}