package com.atguigu.member.service;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Result;
import com.atguigu.common.vo.MemberAddressVo;
import com.atguigu.member.entity.MemberReceiveAddressEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 会员收货地址
 *
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-18 13:42:34
 */
public interface MemberReceiveAddressService extends IService<MemberReceiveAddressEntity> {

    PageUtils queryPage(Map<String, Object> params);
    /**
     * @description: 根据会员id查收获地址列表
     * @param memberId 会员id
     **/
    Result<List<MemberAddressVo>> getAddressListByMemberId(Long memberId);
}

