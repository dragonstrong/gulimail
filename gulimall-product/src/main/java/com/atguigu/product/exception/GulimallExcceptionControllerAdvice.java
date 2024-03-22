package com.atguigu.product.exception;
import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
/**
 * @Author qiang.long
 * @Date 2024/03/22
 * @Description
 **/
@Slf4j
@RestControllerAdvice(basePackages = "com.atguigu.product.controller")
public class GulimallExcceptionControllerAdvice {
    /**
     * @description: 处理数据校验异常
     **/
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handelValidException(MethodArgumentNotValidException e){
        log.error("数据校验出现异常:{},异常类型:{}",e.getMessage(),e.getClass());
        BindingResult bindingResult=e.getBindingResult();
        Map<String ,String> errprMap=new HashMap<>();
        bindingResult.getFieldErrors().forEach(fieldError ->{
            errprMap.put(fieldError.getField(),fieldError.getDefaultMessage());
        } );
        return R.error(BizCodeEnum.VAILD_EXCEPTION.getCode(),BizCodeEnum.VAILD_EXCEPTION.getMsg()).put("data",errprMap);

    }

    /**
     * @description: 处理其他异常
     **/
    @ExceptionHandler(value = Throwable.class)
    public R handelException(Throwable e){
        log.error("出现异常:{},异常类型:{}",e.getMessage(),e.getClass());
        return R.error(BizCodeEnum.UNKNOW_EXCEPTION.getCode(), BizCodeEnum.UNKNOW_EXCEPTION.getMsg());

    }
}
