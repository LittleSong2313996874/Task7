package com.ss.Dao;

import com.ss.pojo.Student;
import com.ss.pojo.User;

public interface UserDao extends GeneralDao<User>{

    //返回1表示添加成功
    int registerUser(User user);

    String isLogin(User user);


}
