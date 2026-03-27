// src/main/java/cn/edu/hdu/controller/AuthController.java
package cn.edu.hdu.controller;

import cn.edu.hdu.pojo.User;
import cn.edu.hdu.service.UserService;
import cn.edu.hdu.util.JwtUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 认证控制器 (Authentication Controller)

 * 如果未来需要将认证逻辑独立出来，请取消下方登录方法的注释，并确保 UserController 中不再定义 /login 接口。
 */
@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UserService userService;

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", message);
        return error;
    }

    @Setter
    @Getter
    public static class LoginRequest {
        private String username;
        private String password;
    }

    /**
     * 💡 扩展建议：
     * 1. 登出接口 (虽然 JWT 通常是无状态的，但可以配合黑名单机制)
     *    @PostMapping("/logout")
     *    public ResponseEntity<Void> logout(...) { ... }

     * 2. 刷新 Token 接口 (当 Token 快过期时，用 Refresh Token 换取新 Access Token)
     *    @PostMapping("/refresh")
     *    public ResponseEntity<Map<String, Object>> refresh(...) { ... }
     *    */
}