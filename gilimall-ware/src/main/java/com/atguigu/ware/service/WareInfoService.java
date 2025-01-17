package com.atguigu.ware.service;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Result;
import com.atguigu.common.vo.FareVo;
import com.atguigu.ware.entity.WareInfoEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 仓库信息
 *
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-18 13:53:59
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
    /**
     * @description: 获取收货地址+运费信息
     * @param addressId 收货地址id
     **/
    Result<FareVo> getFreight(Long addressId);
}

