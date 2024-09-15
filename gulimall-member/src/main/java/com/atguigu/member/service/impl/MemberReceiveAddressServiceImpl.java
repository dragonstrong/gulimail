package com.atguigu.member.service.impl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.common.utils.Result;
import com.atguigu.common.vo.MemberAddressVo;
import com.atguigu.member.dao.MemberReceiveAddressDao;
import com.atguigu.member.entity.MemberReceiveAddressEntity;
import com.atguigu.member.service.MemberReceiveAddressService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
@Service("memberReceiveAddressService")
public class MemberReceiveAddressServiceImpl extends ServiceImpl<MemberReceiveAddressDao, MemberReceiveAddressEntity> implements MemberReceiveAddressService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberReceiveAddressEntity> page = this.page(
                new Query<MemberReceiveAddressEntity>().getPage(params),
                new QueryWrapper<MemberReceiveAddressEntity>()
        );

        return new PageUtils(page);
    }
    @Override
    public Result<List<MemberAddressVo>> getAddressListByMemberId(Long memberId) {
        List<MemberAddressVo> memberAddressVos=Optional.ofNullable(getBaseMapper().selectList(new QueryWrapper<MemberReceiveAddressEntity>().eq("member_id",memberId))).orElse(new ArrayList<>()).stream().map(entity->{
            MemberAddressVo memberAddressVo=new MemberAddressVo();
            BeanUtils.copyProperties(entity,memberAddressVo);
            return memberAddressVo;
        }).collect(Collectors.toList());
        return Result.ok(memberAddressVos);
    }
}