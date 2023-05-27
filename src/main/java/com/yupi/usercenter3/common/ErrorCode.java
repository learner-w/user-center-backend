package com.yupi.usercenter3.common;

/**
 * @author WYW
 * @version 1.0.0
 */
public enum ErrorCode {
   SUCCESS(2000,"ok"," "),
   PARAMS_ERROR(40000,"请求参数错误"," "),
   NULL_ERROR(40001,"请求参数为空",   " "),
   NOT_LOGIN(40100,"用户未登录"," "),
   NO_AUTH(40101,"用户无权限"," "),
    SYSTEM_ERROR(5000,"系统内部异常", " ")
    ;
   /*
    状态码信息
    */
    private final int code;
    /*
    状态码详情
     */
    private final String message;
    private final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
