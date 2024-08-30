package com.atguigu.coupon.service;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.coupon.entity.HomeSubjectEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 首页专题表【jd首页下面很多专题，每个专题链接新的页面，展示专题商品信息】
 *
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-18 13:35:28
 */
public interface HomeSubjectService extends IService<HomeSubjectEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

