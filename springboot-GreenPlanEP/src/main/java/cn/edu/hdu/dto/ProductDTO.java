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

    private List<String> plantingMonths;

    // 兼容 List<String>（来自实体类）
    public void setPlantingMonths(List<String> months) {
        this.plantingMonths = months;
    }

    // 兼容 String（来自数据库原始字符串）
    public void setPlantingMonths(String dbValue) {
        this.plantingMonths = new ArrayList<>();
        if (dbValue != null && !dbValue.isEmpty()) {
            Arrays.stream(dbValue.split(","))
                    .forEach(m -> this.plantingMonths.add(m + "月"));
        }
    }

    private List<String> suitableRegions;

    // 兼容 List<String>（来自实体类）
    public void setSuitableRegions(List<String> regions) {
        this.suitableRegions = regions;
    }

    // 兼容 String（来自数据库原始字符串）
    public void setSuitableRegions(String dbValue) {
        if (dbValue != null && !dbValue.trim().isEmpty()) {
            this.suitableRegions = Arrays.stream(dbValue.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toCollection(ArrayList::new));
        } else {
            this.suitableRegions = new ArrayList<>();
        }
    }
}



