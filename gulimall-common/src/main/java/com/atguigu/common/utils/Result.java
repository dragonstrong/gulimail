package com.atguigu.common.utils;
import com.atguigu.common.exception.BizCodeEnum;

import java.io.Serializable;
/**
 * @Author qiang.long
 * @Date 2024/09/02
 * @Description 泛型R
 **/
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer code;
    private String msg;
    private T data;

    public Result() {
        this.code=0;
        this.msg="success";
    }

    public static Result ok() {
        return new Result();
    }
    public static <T> Result<T> ok(T data) {
        Result r = new Result();
        r.data=data;
        return r;
    }
    public static <T> Result<T> ok(String msg) {
        Result r = new Result();
        r.msg=msg;
        return r;
    }

    public static <T> Result<T> error() {
        return error(BizCodeEnum.UNKNOW_EXCEPTION);
    }
    public static <T> Result<T> error(BizCodeEnum bizCodeEnum) {
        Result r = new Result();
        r.code=bizCodeEnum.getCode();
        r.msg=bizCodeEnum.getMsg();
        return r;
    }
    public Integer getCode(){
        return this.code;
    }
    public String getMsg() {
        return msg;
    }
    public T getData() {
        return data;
    }
}
