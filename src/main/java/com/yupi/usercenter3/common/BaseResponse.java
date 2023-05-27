package com.yupi.usercenter3.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回类
 * @param <T> 返回数据
 */
//基本返回，包含返回的各种信息，如返回状态码、返回数据、返回详细信息等
@Data
public class BaseResponse<T> implements Serializable {
    //状态码
    private int code;
    //具体数据信息
    private T data;
    //返回详细信息
    private String message;

    private String description;

    public BaseResponse(int code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    public BaseResponse(int code, T data, String message) {
     this(code, data, message, " ");
    }

    public BaseResponse(int code, T data) {
        this(code,data,"" , "");
    }

    //错误时的返回信息
    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(),null,errorCode.getMessage(),errorCode.getDescription());
    }
}
