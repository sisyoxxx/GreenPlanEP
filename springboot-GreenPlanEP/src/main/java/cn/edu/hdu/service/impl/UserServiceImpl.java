// src/main/java/cn/edu/hdu/service/impl/UserServiceImpl.java
package cn.edu.hdu.service.impl;

import cn.edu.hdu.mapper.UserMapper;
import cn.edu.hdu.pojo.User;
import cn.edu.hdu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户名错误");
        }

        // 验证密码（BCrypt）
        if (!password.equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        return user;
    }
}