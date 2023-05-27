package com.yupi.usercenter3.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.usercenter3.common.BaseResponse;
import com.yupi.usercenter3.common.ErrorCode;
import com.yupi.usercenter3.common.ResultUtils;
import com.yupi.usercenter3.exception.BusinessException;
import com.yupi.usercenter3.model.domain.User;
import com.yupi.usercenter3.model.request.UserLoginRequest;
import com.yupi.usercenter3.model.request.UserRegisterRequest;
import com.yupi.usercenter3.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static com.yupi.usercenter3.constant.UserConstant.ADMIN_ROLE;
import static com.yupi.usercenter3.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author WYW
 * @version 1.0.0
 */
@RestController
@RequestMapping("/user")
public class UserController {


    @Resource
    private UserService userService;
    //用户注册
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest registerRequest){
        if (registerRequest == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        String userAccount = registerRequest.getUserAccount();
        String userPassword = registerRequest.getUserPassword();
        String checkUserPassword = registerRequest.getCheckUserPassword();
        String planetCode = registerRequest.getPlanetCode();
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkUserPassword)){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        long result = userService.userRegister(userAccount, userPassword, checkUserPassword, planetCode);
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest loginRequest, HttpServletRequest request){
        if (loginRequest == null){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = loginRequest.getUserAccount();
        String userPassword = loginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    @PostMapping("/logout")
    public BaseResponse<String> userLogout(HttpServletRequest request){
        if (request == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        userService.userLogOut(request);
        return ResultUtils.success("注销成功");
    }

    /**
     * 查询用户接口
     */
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String userName, HttpServletRequest request){
        if (!isAdmin(request)){
            throw  new BusinessException(ErrorCode.NO_AUTH);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        if (StringUtils.isNotBlank(userName)){
            queryWrapper.like("userName",userName);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> users = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(users);
    }

    /**
     * 删除用户
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUsers(@RequestBody long id,HttpServletRequest request){
        if (!isAdmin(request)){
            throw  new BusinessException(ErrorCode.NO_AUTH);
        }
        return ResultUtils.success(userService.removeById(id));
    }

    /**
     * 获取用户登录状态
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        //从request地session中取出user
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User)userObj;
        //用户状态可能发生改变，每次获取用户信息时重新从数据库中获取信息
        Long id = currentUser.getId();
        User newUser = userService.getById(id);
        //用户信息脱敏
        User safetyUser = userService.getSafetyUser(newUser);
        return ResultUtils.success(safetyUser);
    }


    /**
     * 判断是否为管理员
     * @return
     */
    private boolean isAdmin(HttpServletRequest request){
        //判断只有管理员可以删除用户信息
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User)userObj;
        if (user == null || user.getUserRole() != ADMIN_ROLE){
            throw  new BusinessException(ErrorCode.NO_AUTH);
        }
        return true;
    }
}
