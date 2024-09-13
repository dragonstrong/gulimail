package com.atguigu.member.service.impl;
import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.common.utils.Result;
import com.atguigu.common.vo.MemberResponseVo;
import com.atguigu.common.vo.UserLoginVo;
import com.atguigu.common.vo.UserRegisterVo;
import com.atguigu.member.dao.MemberDao;
import com.atguigu.member.dao.MemberLevelDao;
import com.atguigu.member.entity.MemberEntity;
import com.atguigu.member.entity.MemberLevelEntity;
import com.atguigu.member.execption.PhoneExistExecption;
import com.atguigu.member.execption.UserNameExistExecption;
import com.atguigu.member.service.MemberService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {
    @Autowired
    MemberLevelDao memberLevelDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }
    @Override
    public Result register(UserRegisterVo userRegisterVo){
        // 手机号和用户名校验：是否已存在
        try{
            checkPhoneUnique(userRegisterVo.getPhone());
            checkUserNameUnique(userRegisterVo.getUserName());
        }catch (PhoneExistExecption e){
            return Result.error(BizCodeEnum.PHONE_EXIST_EXCEPTION);
        }catch (UserNameExistExecption e){
            return Result.error(BizCodeEnum.USERNAME_EXIST_EXCEPTION);
        }

        MemberEntity member=new MemberEntity();
        member.setUsername(userRegisterVo.getUserName());
        member.setMobile(userRegisterVo.getPhone());
        // MD5盐值加密
        // String encode=Md5Crypt.md5Crypt("123456".getBytes(),"$18271&re");
        // 下面这种加密后的字符串中自动带有盐值（在结构中）
        BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
        String encode=passwordEncoder.encode(userRegisterVo.getPassword());
        member.setPassword(encode);
        //String encode=passwordEncoder.encode("123456");
        //boolean matches=passwordEncoder.matches("123456",encode); // 返回true

        // 设置默认会员等级
        MemberLevelEntity defaultLevel=memberLevelDao.selectOne(new QueryWrapper<MemberLevelEntity>().eq("default_status",1));
        if(defaultLevel!=null){
            member.setLevelId(defaultLevel.getId());
        }
        getBaseMapper().insert(member);
        return Result.ok();
    }
    @Override
    public Result<MemberResponseVo> login(UserLoginVo userLoginVo) {
        String loginIdentity=userLoginVo.getLoginIdentity();
        MemberEntity  member=getBaseMapper().selectOne(new QueryWrapper<MemberEntity>().eq("username",loginIdentity).or().eq("mobile",loginIdentity).or().eq("email",loginIdentity));
        if(member==null){
            return Result.error(BizCodeEnum.USER_NOT_EXIST_EXCEPTION);
        }
        String encodePassword=member.getPassword();
        BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
        boolean match=passwordEncoder.matches(userLoginVo.getPassword(), encodePassword);
        if(match){
            MemberResponseVo memberResponseVo=new MemberResponseVo();
            BeanUtils.copyProperties(member,memberResponseVo);
            return Result.ok(memberResponseVo);
        }else {
            return Result.error(BizCodeEnum.PASSWORD_INCORRECT_EXCEPTION);
        }
    }
    /**
     * 检查手机号是否唯一
     **/
    @Override
    public void checkPhoneUnique(String phone) throws PhoneExistExecption{
        Long count=getBaseMapper().selectCount(new QueryWrapper<MemberEntity>().eq("mobile",phone));
        if(count>0){
            throw new PhoneExistExecption();
        }
    }
    /**
     * 检查用户名是否唯一
     **/
    @Override
    public void checkUserNameUnique(String userName) throws UserNameExistExecption{
        Long count=getBaseMapper().selectCount(new QueryWrapper<MemberEntity>().eq("username",userName));
        if(count>0){
            throw new UserNameExistExecption();
        }
    }
}