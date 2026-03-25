package cn.edu.hdu.service.impl;

import cn.edu.hdu.dto.ProductDTO;
import com.github.pagehelper.PageHelper; // 👈 1. 导入 PageHelper
import com.github.pagehelper.PageInfo;   // 👈 2. 导入 PageInfo (用于封装结果)
import cn.edu.hdu.dto.ProductResponse;
import cn.edu.hdu.mapper.ProductMapper;
import cn.edu.hdu.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl extends ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public ProductResponse getProducts(int page, int pageSize, String keyword, String category, String month, String region, String sortBy) {

        // 👇 3. 开启分页 (必须在查询数据库之前调用)
        // 参数：当前页码，每页条数
        PageHelper.startPage(page, pageSize);

        // 👇 4. 执行正常的查询
        // 注意：这里返回的是 List<ProductDTO>，但 PageHelper 会在内部拦截并修改 SQL
        List<ProductDTO> list = productMapper.selectProductsByCondition(keyword, category, month, region, sortBy);

        // 👇 5. 将查询结果封装为 PageInfo 对象 (包含总条数、总页数等信息)
        PageInfo<ProductDTO> pageInfo = new PageInfo<>(list);

        // 👇 6. 封装返回给前端的数据
        return new ProductResponse(
                pageInfo.getList(),       // 当前页数据
                pageInfo.getTotal(),      // 总记录数
                pageInfo.getPageNum(),    // 当前页码
                pageInfo.getPageSize()    // 每页条数
        );
    }
}