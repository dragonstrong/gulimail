package com.atguigu.member.service;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.member.entity.MemberLoginLogEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 会员登录记录
 *
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-18 13:42:34
 */
public interface MemberLoginLogService extends IService<MemberLoginLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

