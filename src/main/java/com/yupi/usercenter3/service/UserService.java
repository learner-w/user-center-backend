package com.yupi.usercenter3.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.usercenter3.model.domain.User;

import javax.servlet.http.HttpServletRequest;

/**
* @author 吴亚伟
* @description 针对表【user(用户信息)】的数据库操作Service
* @createDate 2023-04-24 10:15:25
*/
public interface UserService extends IService<User> {
    /**
     *
     * @param userAccount 账号
     * @param passWord    密码
     * @param checkPassWord 确认密码
     * @return 用户ID
     */
    long userRegister(String userAccount, String passWord, String checkPassWord, String planetCode);

    /**
     *
     * @param userAccount 账号
     * @param passWord    密码
     * @return 返回用户对象
     */
    User userLogin(String userAccount, String passWord,HttpServletRequest request);


    /**
     *
     * @param originUser 原始用户信息
     * @return 脱敏后的用户信息
     */
    User getSafetyUser(User originUser);

    /**
     *
     * @param request 通过request传递登录信息
     * @return
     */
    int userLogOut(HttpServletRequest request);
}
