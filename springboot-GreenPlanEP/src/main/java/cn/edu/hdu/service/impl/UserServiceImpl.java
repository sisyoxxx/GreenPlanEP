package cn.edu.hdu.service.impl;

import cn.edu.hdu.enums.UserRole;
import cn.edu.hdu.mapper.UserMapper;
import cn.edu.hdu.pojo.User;
import cn.edu.hdu.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String username, String password) {
        // 1. 根据用户名查询
        User user = userMapper.findByUsername(username);
        System.out.println("登录提交的用户信息: " + user);
        // 2. 判断用户是否存在
        if (user == null) {
            throw new RuntimeException("用户名不存在");
        }

        // 3. 验证密码 (明文比对)
        if (user.getPassword() == null) {
            throw new RuntimeException("用户凭证无效，请重置密码");
        }
        // 这里使用 user.getPassword().equals(password) 或 Objects.equals
        // 使用 Objects.equals，因为它能处理 null 值
        if (!Objects.equals(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 4. 登录成功，返回用户对象
        return user;
    }

    @Override
    public User register(User user) {


        System.out.println("Step 0: User 对象本身: " + (user != null ? "存在" : "NULL"));
        // 1. 基础校验
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new RuntimeException("密码长度至少为6位");
        }

        System.out.println("【调试】即将查询数据库前的用户信息: " + user);
        System.out.println("\n");

        // 2. 检查用户名是否已存在
        User existingUser = userMapper.selectOne(
                new QueryWrapper<User>()
                        .eq("username", user.getUsername()) // 直接传值，避免调用 getter
        );
        if (existingUser != null) {
            throw new RuntimeException("用户名已被注册");
        }



        // 3. 设置默认值
        user.setRole(UserRole.BUYER); // 默认角色

        user.setCreatedAt(LocalDateTime.now()); // 当前时间
        try {
            // 这是一个简单的 SQL，看能否通
            Integer count = Math.toIntExact(userMapper.selectCount(null));
            System.out.println("数据库连接测试成功，用户表总记录数: " + count);
        } catch (Exception e) {
            System.out.println("数据库连接失败！！！");
            e.printStackTrace(); // 这里强制打印堆栈，看看到底是什么错
        }


        System.out.println("加工后的用户信息: " + user);

        // 4. 插入数据库
        int rows = userMapper.insert(user);
        if (rows <= 0) {
            throw new RuntimeException("注册失败，数据库写入错误");
        }

        // 5. 返回注册成功的用户信息
        return user;
    }
}