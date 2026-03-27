package cn.edu.hdu.mapper;

import cn.edu.hdu.pojo.Product;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    // 不需要任何额外代码，BaseMapper 已提供 selectPage, insert, selectById 等方法
}