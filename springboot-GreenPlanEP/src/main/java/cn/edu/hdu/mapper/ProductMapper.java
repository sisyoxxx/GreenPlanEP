package cn.edu.hdu.mapper;

import cn.edu.hdu.dto.ProductDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper{
    int insert(ProductDTO product);
    List<ProductDTO> selectProductsByCondition(
            @Param("keyword") String keyword,
            @Param("category") String category,
            @Param("month") String month,
            @Param("region") String region,
            @Param("sortBy") String sortBy
    );
}
