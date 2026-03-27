// src/main/java/cn/edu/hdu/service/UserService.java
package cn.edu.hdu.service;

import cn.edu.hdu.pojo.User;

// 1. 这是一个接口，没有 @Service 注解
public interface UserService {

    // 定义登录方法
    User login(String username, String password);

    // 定义注册方法
    User register(User user);
}