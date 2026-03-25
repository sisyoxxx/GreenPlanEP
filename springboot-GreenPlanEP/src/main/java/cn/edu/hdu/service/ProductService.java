package cn.edu.hdu.service;

import cn.edu.hdu.dto.ProductDTO;
import cn.edu.hdu.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.edu.hdu.mapper.ProductMapper; // 确保你有这个 Mapper

import java.time.LocalDateTime;

@Service // 1. 告诉 Spring 这是一个服务组件
public abstract class ProductService {

    @Autowired
    private ProductMapper productMapper; // 2. 注入 Mapper 操作数据库

    /**
     * 保存商品到数据库
     */
    @Transactional
    public ProductDTO saveProduct(ProductDTO product) {
        if (product == null || product.getName() == null) {
            throw new IllegalArgumentException("商品数据不能为空");
        }

        // 确保创建时间存在
        if (product.getCreateTime() == null) {
            product.setCreateTime(LocalDateTime.now());
        }

        // 调用 Mapper 插入数据 (需要在 ProductMapper.xml 中配置好 insert 语句)
        int rows = productMapper.insert(product);

        if (rows <= 0) {
            throw new RuntimeException("数据库插入失败");
        }

        return product; // 返回包含生成 ID 的对象
    }

    public abstract ProductResponse getProducts(int page, int pageSize, String keyword, String category, String month, String region, String sortBy);
}