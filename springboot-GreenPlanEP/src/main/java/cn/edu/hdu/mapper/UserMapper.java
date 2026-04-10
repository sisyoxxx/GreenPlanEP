// src/main/java/cn/edu/hdu/mapper/UserMapper.java
package cn.edu.hdu.mapper;

import cn.edu.hdu.pojo.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    User findByUsername(@Param("username") String username);
}