package com.ss.service;

import com.ss.Dao.UserDao;
import com.ss.pojo.User;
import org.hibernate.validator.constraints.Email;
import org.springframework.ui.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface UserService {

    //  注册
    int registerUser(User user);

    String isLogin(User user);

    int updateObject(User t);

    User getObjectById(int id);

    //发送手机注册验证短信
    Map sendPhoneCode(User user) throws Exception;

    //发送激活邮件
    Map sendLinkOrCodeByMail(User user, String basePath) throws Exception;


}
