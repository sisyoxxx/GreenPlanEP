package cn.edu.hdu.pojo;

import cn.edu.hdu.enums.UserRole;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("users") // 【重要】告诉 MP 这个类对应数据库的 users 表
public class User {

    @TableId(type = IdType.AUTO) // 【重要】指定主键策略为自增
    private Long id;
    @TableField("username")
    private String username;
    @TableField("password")
    private String password;

    @TableField("role")
    private UserRole role;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}