// src/main/java/cn/edu/hdu/mapper/UserMapper.java
package cn.edu.hdu.mapper;

import cn.edu.hdu.pojo.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 不需要写任何方法，BaseMapper 已经实现了 CRUD
}