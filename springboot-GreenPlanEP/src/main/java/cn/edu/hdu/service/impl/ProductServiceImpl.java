package cn.edu.hdu.service.impl;

import cn.edu.hdu.dto.ProductDTO;
import cn.edu.hdu.pojo.Product;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.edu.hdu.dto.ProductResponse;
import cn.edu.hdu.mapper.ProductMapper;
import cn.edu.hdu.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl extends ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public ProductResponse getProducts(int page, int pageSize, String keyword, String category, String month, String region, String sortBy) {

        // 👇 1. 创建 MyBatis-Plus 的分页对象
        // 参数：当前页码，每页条数
        Page<Product> productPage = new Page<>(page, pageSize);

        // 👇 2. 构建查询条件 (LambdaQueryWrapper)
        // 替代原本 XML 中的动态 SQL 逻辑
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        // 模糊查询关键词 (假设匹配名称或描述)
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Product::getName, keyword)
                    .or()
                    .like(Product::getShortDescription, keyword));
        }

        // 精确查询分类
        if (StringUtils.isNotBlank(category)) {
            wrapper.eq(Product::getCategory, category);
        }

        // 月份筛选：使用 LIKE 匹配逗号分隔的字符串，如“4”匹配“3,4,5”
        if (StringUtils.isNotBlank(month)) {
            wrapper.like(Product::getPlantingMonths, month);
        }

        // 地区筛选：使用 LIKE 匹配逗号分隔的字符串，如“华北”匹配“华北,华东”
        if (StringUtils.isNotBlank(region)) {
            wrapper.like(Product::getSuitableRegions, region);
        }

        // 排序处理
        if (StringUtils.isNotBlank(sortBy)) {
            // 根据传入字符串决定升序还是降序
            if ("price_asc".equals(sortBy)) {
                wrapper.orderByAsc(Product::getPrice);
            } else if ("price_desc".equals(sortBy)) {
                wrapper.orderByDesc(Product::getPrice);
            } //这里差一个销量还没做  else if ("sales_desc".equals(sortBy)) { //
            // wrapper.orderByDesc(Product::getSalesVolume);
            else {
                // 默认按创建时间倒序
                wrapper.orderByDesc(Product::getCreateTime);
            }
        } else {
            // 默认排序
            wrapper.orderByDesc(Product::getCreateTime);
        }

        // 👇 3. 执行分页查询
        // productMapper 需要继承 BaseMapper<Product>
        // selectPage 会自动生成分页 SQL (LIMIT ...) 和 count SQL
        IPage<Product> resultPage = productMapper.selectPage(productPage, wrapper);

        // 👇 4. 数据转换 (Entity -> DTO)
        // MP 返回的是 Product 实体列表，你需要手动转为 ProductDTO
        // 如果你的业务逻辑很复杂，也可以在这里调用一个 convertList 方法
        List<ProductDTO> dtoList = resultPage.getRecords().stream()
                .map(this::convertToDTO) // 假设你有一个私有方法做转换
                .collect(Collectors.toList());

        // 👇 5. 封装返回给前端的数据
        return new ProductResponse(
                dtoList,                    // 当前页数据 (已转为 DTO)
                resultPage.getTotal(),      // 总记录数
                (int) resultPage.getCurrent(), // 当前页码
                (int) resultPage.getSize()     // 每页条数
        );
    }

    // 辅助方法：实体转 DTO (根据你的实际字段映射填写)
    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setCategory(product.getCategory());
        dto.setStock(product.getStock());
        dto.setDifficulty(product.getDifficulty());
        dto.setShortDescription(product.getShortDescription());
        dto.setDetailDescription(product.getDetailDescription());
        dto.setPlantingMonths(product.getPlantingMonths());
        dto.setSuitableRegions(product.getSuitableRegions());
        dto.setImageUrl(product.getImageUrl());
        return dto;
    }
}