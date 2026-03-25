// src/main/java/cn/edu/hdu/controller/AuthController.java
package cn.edu.hdu.controller;

import cn.edu.hdu.pojo.User;
import cn.edu.hdu.service.UserService;
import cn.edu.hdu.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 登录接口
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {

        // 1. 参数校验
        if (request == null ||
                Objects.isNull(request.getUsername()) ||
                Objects.isNull(request.getPassword())) {
            return ResponseEntity.badRequest()
                    .body(createErrorResponse("用户名和密码不能为空"));
        }

        try {
            // 2. 调用服务层登录
            User user = userService.login(request.getUsername(), request.getPassword()); // ← 传入明文
            // 3. 生成 token
            String token = JwtUtil.generateToken(user.getUsername());

            // 4. 构造成功响应
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("name", user.getName());
            return ResponseEntity.ok(result);

        } catch (RuntimeException e) {
            // 5. 捕获业务异常（如用户不存在、密码错误）
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    // 辅助方法：构造错误响应
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", message);
        return error;
    }

    // 内部 DTO 类（也可单独建文件）
    public static class LoginRequest {
        private String username;
        private String password;

        // Getters and Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }


}