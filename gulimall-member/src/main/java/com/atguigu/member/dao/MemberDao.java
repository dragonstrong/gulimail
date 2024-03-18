package com.atguigu.member.dao;

import com.atguigu.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-18 13:42:34
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
