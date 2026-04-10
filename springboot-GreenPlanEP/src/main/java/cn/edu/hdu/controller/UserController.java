package cn.edu.hdu.controller;

import cn.edu.hdu.common.Result;
import cn.edu.hdu.pojo.User;
import cn.edu.hdu.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> map) {
        String username = map.get("username");
        String password = map.get("password");

        try {
            User user = userService.login(username, password);
            Map<String, Object> data = new HashMap<>();
            data.put("token", UUID.randomUUID().toString());
            data.put("user", safeUser(user));
            return Result.success(data);
        } catch (RuntimeException e) {
            e.printStackTrace();
            String message = e.getMessage();
            if (message == null || message.isBlank()) {
                message = "登录失败，请稍后重试";
            }
            return Result.custom(400, message);
        }
    }

    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody User user) {
        try {
            User newUser = userService.register(user);
            Map<String, Object> data = new HashMap<>();
            data.put("user", safeUser(newUser));
            return Result.success(data);
        } catch (RuntimeException e) {
            e.printStackTrace();
            String message = e.getMessage();
            if (message == null || message.isBlank()) {
                message = "注册失败，请稍后重试";
            }
            return Result.custom(400, message);
        }
    }

    private Map<String, Object> safeUser(User user) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("username", user.getUsername());
        userData.put("role", user.getRole());
        userData.put("createdAt", user.getCreatedAt());
        return userData;
    }
}
