package com.yupi.usercenter3.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author WYW
 * @version 1.0.0
 */
@Data
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = 3072976979148446617L;
    private String userAccount;
    private String userPassword;
    private String checkUserPassword;
    private String planetCode;
}
