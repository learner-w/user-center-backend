package com.yupi.usercenter3.model.request;

import lombok.Data;
import java.io.Serializable;

/**
 * @author WYW
 * @version 1.0.0
 */
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = -2330078262728075265L;
    private String userAccount;
    private String userPassword;
}
