package cn.edu.hdu.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
public class ProductDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private String category;
    private Integer stock;
    private Integer difficulty;
    private String imageUrl;
    private String shortDescription;
    private String detailDescription;
    private LocalDateTime createTime;

    //种植月份和地区方法
    private List<String> plantingMonths;
    public void setPlantingMonths(String dbValue) {
        this.plantingMonths = new ArrayList<>();
        if (dbValue != null && !dbValue.isEmpty()) {
            // "3,4,5" -> ["3月", "4月", "5月"] (根据需求加"月"字)
            Arrays.stream(dbValue.split(","))
                    .forEach(m -> this.plantingMonths.add(m + "月"));
        }
    }

    private List<String> suitableRegions;
    public void setSuitableRegions(String dbValue) {
        if (dbValue != null && !dbValue.trim().isEmpty()) {
            this.suitableRegions = Arrays.stream(dbValue.split(","))
                    .map(String::trim)          // 去除每个元素前后的空格
                    .filter(s -> !s.isEmpty())  // 过滤掉空字符串（防止 ",," 的情况）
                    .collect(Collectors.toCollection(ArrayList::new)); // 收集到 ArrayList
        } else {
            this.suitableRegions = new ArrayList<>(); // 防止 null，给个空列表
        }
    }
}



