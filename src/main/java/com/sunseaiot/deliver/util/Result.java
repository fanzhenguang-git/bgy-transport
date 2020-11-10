/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.util;


import com.sunseaiot.deliver.exception.DeliverError;
import lombok.Data;

/**
 * 接口返回参数封装类
 *
 * @Author: wanglong
 * @Date: 2020/1/10
 **/
@Data
public class Result<T> {
    // error_code 状态值：0 极为成功，其他数值代表失败
    private Integer code;

    // error_msg 错误信息，若code为0时，为success
    private String msg;

    // content 返回体报文的出参，使用泛型兼容不同的类型
    private T result;

    public Result putResult(T result) {
        setResult(result);
        return this;
    }

    /**
     * 返回成功，传入返回体具体出參
     *
     * @param object
     * @return
     */
    public static Result ok(Object object) {
        Result response = new Result();
        response.setCode(0);
        response.setMsg("ok");
        response.setResult(object);
        return response;
    }

    /**
     * 提供给部分不需要出參的接口
     *
     * @return
     */
    public static Result ok() {
        return ok(null);
    }

    /**
     * 自定义错误信息
     *
     * @param code
     * @param msg
     * @return
     */
    public static Result error(Integer code, String msg) {
        Result response = new Result();
        response.setCode(code);
        response.setMsg(msg);
        response.setResult(null);
        return response;
    }

    /**
     * 返回异常信息，在已知的范围内
     *
     * @param errorCodeEnum
     * @return
     */
    public static Result error(DeliverError errorCodeEnum) {
        Result response = new Result();
        response.setCode(errorCodeEnum.getCode());
        response.setMsg(errorCodeEnum.getMessage());
        response.setResult(null);
        return response;
    }

    /**
     * 返回异常信息
     *
     * @param msg
     * @return
     */
    public static Result error(String msg) {
        Result response = new Result();
        response.setCode(-1);
        response.setMsg(msg);
        response.setResult(null);
        return response;
    }

    @Override
    public String toString() {
        return "Result{" + "code=" + code + ", msg='" + msg + '\'' + ", result=" + result + '}';
    }
}
