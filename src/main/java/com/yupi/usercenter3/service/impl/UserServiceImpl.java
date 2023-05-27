package com.yupi.usercenter3.service.impl;
import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.usercenter3.common.ErrorCode;
import com.yupi.usercenter3.exception.BusinessException;
import com.yupi.usercenter3.mapper.UserMapper;
import com.yupi.usercenter3.model.domain.User;
import com.yupi.usercenter3.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.yupi.usercenter3.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author 吴亚伟
* @description 针对表【user(用户信息)】的数据库操作Service实现
* @createDate 2023-04-24 10:15:25
*/
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{
    //加SALT以混淆加密
    private static final String SALT = "being";
    @Resource
    public UserMapper userMapper;
    @Override
    public long userRegister(String userAccount, String passWord, String checkPassWord, String planetCode) {
        //在出错时给出提示信息
        String errorMsg;
        //1.1、校验账户、密码、校验密码不为空
        if (StringUtils.isAnyBlank(userAccount,passWord,checkPassWord)){
            errorMsg = "账户、密码、校验密码不为空";
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户、密码、校验密码不为空");
        }

        //1.2.1、账户长度不小于四位
        if (userAccount.length() < 4){
            errorMsg = "账户长度不小于四位";
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户长度不小于四位");
        }

        //1.2.2 星球编号长度不大于五位
        if (planetCode.length() > 5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球编号不能大于五位");
        }

        //1.3.1、账户名不能重复
        //先根据用户的账户名在数据库中查询，根据查询的结果判断账户名是否重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        Long count = userMapper.selectCount(queryWrapper);
        if (count > 0){
            errorMsg = "账户名不能重复";
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户名不能重复");
        }

        //1.3.2 星球编号不能重复
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planetCode",planetCode);
        count = userMapper.selectCount(queryWrapper);
        if (count > 0){
            errorMsg = "星球编号不能不能重复";
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球编号不能不能重复");
        }

        //1.4、密码长度不小于8位
        if (passWord.length() < 8){
            errorMsg = "密码长度不小于8位";
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码长度不小于8位");
        }

        //1.5、密码不能包含特殊字符（使用正则表达式）
        String validRule = "[`~!@#$%^&*()+=|{}':;',\\\\\\\\[\\\\\\\\].<>/?~！@#￥%……\n" +
                "&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validRule).matcher(userAccount);
        if (matcher.find()){
            errorMsg = "密码不能包含特殊字符";
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码不能包含特殊字符");
        }

        //1.6、两次密码是否相同
        if (!(passWord.equals(checkPassWord))){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次输入的密码不相同");
        }

        //2、对密码进行加密
        String encPassWord = DigestUtils.md5DigestAsHex((SALT + passWord).getBytes(StandardCharsets.UTF_8));

        //3、所有校验通过，向数据库中插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encPassWord);
        user.setPlanetCode(planetCode);
        int result = userMapper.insert(user);
        //errorMsg = "向数据库中插入信息时出现错误";
        if (result < 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"向数据库中插入信息时出现错误");
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String passWord, HttpServletRequest request) {
        //1.1、校验账户、密码、校验密码不为空
        if (StringUtils.isAnyBlank(userAccount,passWord)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户、密码、校验密码不为空");
        }
        //1.2、账户长度不小于四位
        if (userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户长度不小于四位");
        }
        //1.3、密码长度不小于8位
        if (passWord.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码长度不小于八位");
        }
        //1.4、密码不能包含特殊字符（使用正则表达式）
        String validRule = "[`~!@#$%^&*()+=|{}':;',\\\\\\\\[\\\\\\\\].<>/?~！@#￥%……\n" +
                "&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validRule).matcher(userAccount);
        if (matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码不能包含特殊字符");
        }
        //2、对密码进行加密
        String encPassWord = DigestUtils.md5DigestAsHex((SALT + passWord).getBytes());
        //查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",encPassWord);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null){
            log.info("user login failed, wrong account or password.");
            return null;
        }

        //3、因为上面的user是从数据库中直接查询出来的需要对用户的数据进行脱敏
        //只保留必要信息
        User safetyUser = getSafetyUser(user);
        //4、记录用户的登录状态
        request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);
        return safetyUser;
    }

    /**
     * 用户信息脱敏
     * @param user 原始用户信息
     * @return  脱敏后的用户信息
     */
    @Override
    public User getSafetyUser(User user) {
        if (user == null){
            return null;
        }
        User safeUser = new User();
        safeUser.setId(user.getId());
        safeUser.setUserName(user.getUserName());
        safeUser.setUserAccount(user.getUserAccount());
        safeUser.setAvatarUrl(user.getAvatarUrl());
        safeUser.setGender(user.getGender());
        safeUser.setCreatTime(new Date());
        safeUser.setPhone(user.getPhone());
        safeUser.setPlanetCode(user.getPlanetCode());
        safeUser.setUserRole(user.getUserRole());
        safeUser.setUserStatus(user.getUserStatus());
        safeUser.setEmail(user.getEmail());
        safeUser.setUpdateTime(new Date());
        return safeUser;
    }


    /**
     *
     * @param request 通过request传递登录信息
     * @return
     */
    @Override
    public int userLogOut(HttpServletRequest request) {
        //从Session里移除登录相关信息
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

}




