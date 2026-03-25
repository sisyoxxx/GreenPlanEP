// src/main/java/cn/edu/hdu/service/UserService.java
package cn.edu.hdu.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import cn.edu.hdu.mapper.UserMapper;
import cn.edu.hdu.pojo.User;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User login(String username, String password) {
        // 1. 查询用户
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 直接比对明文密码（⚠️ 仅限开发测试！）
        if (!password.equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        return user;
    }
}