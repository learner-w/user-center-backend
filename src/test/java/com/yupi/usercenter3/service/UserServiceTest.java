package com.yupi.usercenter3.service;

import com.yupi.usercenter3.mapper.UserMapper;
import com.yupi.usercenter3.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author WYW
 * @version 1.0.0
 */
//测试UserService里的功能是否可用
@SpringBootTest
public class UserServiceTest {
    @Resource
    public UserMapper userMapper;
    @Resource
    public UserService userService;

    @Test
    public void testAddUser() {
        User user = new User();
        user.setUserName("dogMe2");
        user.setUserAccount("112");
        user.setAvatarUrl("http://pic.pdowncc.com/upload/2015-5/201551495226.png");
        user.setGender(0);
        user.setUserPassword("1231");
        user.setCreatTime(new Date());
        user.setPhone("12313");
        user.setUserStatus(0);
        user.setEmail("1231");
        user.setUpdateTime(new Date());
        user.setIsDelete(0);
        int result = userMapper.insert(user);
        System.out.println(result);
        Assertions.assertEquals(1, result);
    }


    @Test
    void userRegister() {
        String userName = "dogMe04123";
        String passWord = "123456789";
        String checkPassWord = "123456789";
        String planetCode = "123";
        long result = userService.userRegister(userName, passWord, checkPassWord, planetCode);
        Assertions.assertEquals(-1,result);
    }
}