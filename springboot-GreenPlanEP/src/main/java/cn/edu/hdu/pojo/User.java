package cn.edu.hdu.pojo;

import cn.edu.hdu.enums.UserRole;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName; // 关键注解：指定表名
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("users") // 【重要】告诉 MP 这个类对应数据库的 users 表
public class User {

    @TableId(type = IdType.AUTO) // 【重要】指定主键策略为自增
    private Long id;
    @TableField
    private String username;
    @TableField
    private String password;
    @TableField
    private UserRole role;
    @TableField("fill = FieldFill.INSERT")
    private LocalDateTime createdAt;
}