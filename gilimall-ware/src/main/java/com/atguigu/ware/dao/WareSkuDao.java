package com.atguigu.ware.dao;

import com.atguigu.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 * 商品库存
 * 
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-18 13:53:59
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
    /**
     * @description: 采购项入库
     * @param:
     * @param skuId
     * @param wareId 仓库id
     * @param skuNum 数量
     * @return: void
     **/
    void addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);
    /**
     * @description: 查询当前sku是否有库存  = 总量-总锁定量  ，一个skuId可能分布在多个仓库
     * @param:
     * @param skuId
     * @return: Long
     **/
    Long getSkuStock(@Param("skuId") Long skuId);
    /**
     * @description:  找出有库存的仓库id
     * @param:
     * @param skuId
     * @return: List<Long>
     **/
    List<Long> listWareIdsHasStock(@Param("skuId") Long skuId);
    /**
     * @description:  锁库存，更新成功返回1，失败返回0
     * @param:
     * @param skuId 商品id
     * @param wareId 仓库id
     * @param num 锁定数量
     * @return: Long
     **/
    Long lockSkuStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId,@Param("num") Integer num);
}
