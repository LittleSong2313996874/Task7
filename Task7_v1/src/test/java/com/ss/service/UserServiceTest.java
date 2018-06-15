package com.ss.service;

import com.ss.cache.RedisCache;
import com.ss.pojo.User;
import com.ss.util.third_party_api.EmailUtil;
import com.ss.util.third_party_api.OSSUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class UserServiceTest {


    @Autowired
    UserService userService;

    @Autowired
    VocationService vocationService;

    @Test
    public void vocationTest() {

        System.out.println(RedisCache.getRandemTime()/60);   // 495 500 490 510 505 515

    }


    @Test
    public void registerUser() {
        User user = new User();
        user.setUsername("小华");
        user.setPassword("1088762bd10ac5d1f135ce61263ada37");

        int i = userService.registerUser(user);

        System.out.println(i+"");
    }

    @Test
    public void isLogin() {
        User user = new User();
        user.setUsername("小明");
        user.setPassword("a3faa79dd05703cf6d43f585077fe74b");
        String i =  userService.isLogin(user);
        System.out.println(i);
    }

    @Test
    public void update() {
        User user = new User();
        user.setId(22);
        user.setPortrait("http://image-ls.oss-cn-beijing.aliyuncs.com/portrait/%E7%BB%B4%E5%A4%9A%E5%88%A9%E4%BA%9A_20180611134922.PNG?Expires=1528732162&OSSAccessKeyId=LTAIVJzt1ZhVHhNU&Signature=MfYuEc06MYbrWnYVtHHd27JNRqU%3D&x-oss-process=image%2Fresize%2Cm_lfit%2Ch_200%2Cw_200");
        int i =  userService.updateObject(user);
        System.out.println(i);
    }

    @Test
    public void upload() {
        /*String url = OSSUtil.getImgUrl("portrait/TEST_NAME_20180608142559_v1.jpg");
        System.out.println(url);*/

        OSSUtil.getImgUrlPrivate("portrait/TEST_NAME_20180608142559_v1.jpg");


    }
    @Test
    public void getById() {
       User user = userService.getObjectById(14);
        System.out.println(user);
    }
    @Test
    public void getById2() {
        User user = new User();
        user.setPassword("e5907f2617fb0d7c3edbfd3fa58534bd");
        user.setUsername("维多利亚");
        String i  = userService.isLogin(user);
        System.out.println(i);
    }

}