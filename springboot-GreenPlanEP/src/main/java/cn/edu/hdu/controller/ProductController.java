package cn.edu.hdu.controller;

import cn.edu.hdu.dto.ProductResponse;
import cn.edu.hdu.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api") // 统一前缀
@CrossOrigin(origins = "*") // 【重要】允许前端跨域访问，否则前端会报 CORS 错误
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 获取商品列表
     * URL: /api/products?page=1&pageSize=12&keyword=&category=&sortBy=default
     */
    @GetMapping("/products")
    public ProductResponse getProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int pageSize,
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false, defaultValue = "") String category,
            @RequestParam(required = false, defaultValue = "") String month,
            @RequestParam(required = false, defaultValue = "") String region,
            @RequestParam(defaultValue = "default") String sortBy
    ) {
        // 清洗月份：去掉“月”字，如“4月” -> “4”
        String cleanMonth = month != null && month.contains("月") ? month.replace("月", "") : month;

        // 清洗地区：去掉空格，如“ 华北 ” -> “华北”
        String cleanRegion = region != null ? region.trim() : region;

        // 调用 Service 层
        return productService.getProducts(page, pageSize, keyword, category, cleanMonth, cleanRegion, sortBy);
    }
}
