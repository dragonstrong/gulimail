package com.atguigu.ware.service;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.ware.entity.PurchaseEntity;
import com.atguigu.ware.vo.MergePurchaseVo;
import com.atguigu.ware.vo.PurchaseFinishVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-18 13:53:59
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);
    PageUtils queryPageUnreceive(Map<String, Object> params);
    /**
     * @description: 合并采购单
     **/
    void merge(MergePurchaseVo mergePurchaseVo);
    /**
     * @description: 领取采购单
     **/
    void receive(List<Long> ids);
    /**
     * @description: 完成采购单
     **/
    void finish(PurchaseFinishVo purchaseFinishVo);
}

