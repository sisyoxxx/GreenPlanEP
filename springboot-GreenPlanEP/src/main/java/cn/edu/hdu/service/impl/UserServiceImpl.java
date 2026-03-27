// src/main/java/cn/edu/hdu/service/impl/UserServiceImpl.java
package cn.edu.hdu.service.impl;

import cn.edu.hdu.mapper.UserMapper;
import cn.edu.hdu.pojo.User;
import cn.edu.hdu.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service // 2. 只有实现类加 @Service
public class UserServiceImpl implements UserService { // 3. 使用 implements

    @Autowired
    private UserMapper userMapper;

    /**
     * 登录逻辑
     */
    @Override
    public User login(String username, String password) {
        // 1. 根据用户名查询
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, username)
        );

        // 2. 判断用户是否存在
        if (user == null) {
            throw new RuntimeException("用户名不存在");
        }

        // 3. 验证密码 (明文比对)
        // 注意：你之前的注释写了 BCrypt，但代码用的是 equals，这里保持明文逻辑
        if (!password.equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 4. 登录成功，返回用户对象（通常不返回密码字段，但在毕设中可以直接返回）
        return user;
    }

    /**
     * 注册逻辑
     */
    @Override
    public User register(User user) {
        // 1. 基础校验
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new RuntimeException("密码长度至少为6位");
        }

        // 2. 检查用户名是否已存在
        User existingUser = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, user.getUsername())
        );
        if (existingUser != null) {
            throw new RuntimeException("用户名已被注册");
        }

        // 3. 设置默认值
        user.setRole("user"); // 默认角色
        user.setCreatedTime(LocalDateTime.now()); // 当前时间

        // 4. 插入数据库
        int rows = userMapper.insert(user);
        if (rows <= 0) {
            throw new RuntimeException("注册失败，数据库写入错误");
        }

        // 5. 返回注册成功的用户信息
        return user;
    }
}