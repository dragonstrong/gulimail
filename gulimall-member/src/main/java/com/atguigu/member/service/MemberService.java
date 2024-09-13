package com.atguigu.member.service;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Result;
import com.atguigu.common.vo.MemberResponseVo;
import com.atguigu.common.vo.UserLoginVo;
import com.atguigu.common.vo.UserRegisterVo;
import com.atguigu.member.entity.MemberEntity;
import com.atguigu.member.execption.PhoneExistExecption;
import com.atguigu.member.execption.UserNameExistExecption;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 会员
 *
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-18 13:42:34
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);
    /**
     * @description: 用户注册
     * @param:
     * @param userRegisterVo 注册信息
     * @return: com.atguigu.common.utils.Result
     **/
    Result register(UserRegisterVo userRegisterVo);
    /**
     * @description: 用户登录
     **/
    Result<MemberResponseVo> login(UserLoginVo userLoginVo);
    void checkPhoneUnique(String phone) throws PhoneExistExecption;
    void checkUserNameUnique(String userName) throws UserNameExistExecption;
}

