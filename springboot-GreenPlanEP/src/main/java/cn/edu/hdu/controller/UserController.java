// src/main/java/cn/edu/hdu/controller/UserController.java
package cn.edu.hdu.controller;

import cn.edu.hdu.pojo.User;
import cn.edu.hdu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService; // 注入的是接口

    // 登录接口
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginData) {
        Map<String, Object> result = new HashMap<>();
        try {
            String username = loginData.get("username");
            String password = loginData.get("password");

            User user = userService.login(username, password);

            result.put("code", 200);
            result.put("msg", "登录成功");
            result.put("data", user);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    // 注册接口
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            User newUser = userService.register(user);

            result.put("code", 200);
            result.put("msg", "注册成功");
            result.put("data", newUser);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("msg", e.getMessage());
        }
        return result;
    }
}