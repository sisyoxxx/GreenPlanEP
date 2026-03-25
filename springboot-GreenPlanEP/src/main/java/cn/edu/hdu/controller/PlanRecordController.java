package cn.edu.hdu.controller;

import cn.edu.hdu.dto.ProductDTO; // 1. 导入移出来的 DTO
import cn.edu.hdu.service.ProductService; // 2. 导入 Service
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/products")
@CrossOrigin(origins = "*")
public class PlanRecordController {

    // 3. 注入 Service (Spring 会自动处理)
    @Autowired
    private ProductService productService;

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads";

    @PostMapping("/add")
    public ApiResponse<ProductDTO> addProduct(
            @RequestParam("image") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("price") BigDecimal price,
            @RequestParam("category") String category,
            @RequestParam("shortDescription") String shortDescription,
            @RequestParam(value = "detailDescription", required = false) String detailDescription,
            @RequestParam("difficulty") Integer difficulty,
            @RequestParam("stock") Integer stock,
            @RequestParam("plantingMonths") String plantingMonths,
            @RequestParam("suitableRegions") String suitableRegions
    ) {
        if (file.isEmpty()) return ApiResponse.error("必须上传商品主图");
        if (name == null || name.trim().isEmpty()) return ApiResponse.error("商品名称不能为空");

        try {
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) dir.mkdirs();

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";

            String newFilename = UUID.randomUUID().toString() + extension;
            Path filePath = Paths.get(UPLOAD_DIR, newFilename);
            Files.write(filePath, file.getBytes());

            String imageUrl = "/uploads/" + newFilename;

            // 构造 DTO
            ProductDTO product = new ProductDTO();
            product.setName(name.trim());
            product.setPrice(price);
            product.setCategory(category);
            product.setShortDescription(shortDescription);
            product.setDetailDescription(detailDescription != null ? detailDescription : "");
            product.setDifficulty(difficulty);
            product.setStock(stock);
            product.setImageUrl(imageUrl);
            product.setPlantingMonths(plantingMonths);
            product.setSuitableRegions(suitableRegions);
            product.setCreateTime(LocalDateTime.now());

            // ============================================================
            // 使用注入的对象调用方法
            // ============================================================
            productService.saveProduct(product);

            return ApiResponse.success(product);

        } catch (IOException e) {
            e.printStackTrace();
            return ApiResponse.error("文件保存失败：" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("发布失败：" + e.getMessage());
        }
    }

    // ... (ApiResponse 内部类保持不变) ...
    @Setter
    @Getter
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;
        public static <T> ApiResponse<T> success(T data) {
            ApiResponse<T> res = new ApiResponse<>();
            res.success = true;
            res.message = "成功";
            res.data = data;
            return res;
        }
        public static <T> ApiResponse<T> error(String message) {
            ApiResponse<T> res = new ApiResponse<>();
            res.success = false;
            res.message = message;
            res.data = null;
            return res;
        }
    }

}