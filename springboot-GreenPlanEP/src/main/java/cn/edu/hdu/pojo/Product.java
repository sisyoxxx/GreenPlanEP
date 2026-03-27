package cn.edu.hdu.pojo; // 请根据你的实际包名调整

import cn.edu.hdu.handler.AbstractListTypeHandler;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("products") // 数据库表名
public class Product implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO) // 主键自增
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

    //@TableField(typeHandler = AbstractListTypeHandler.class)
    private List<String> plantingMonths;

    //@TableField(typeHandler = AbstractListTypeHandler.class)
    private List<String> suitableRegions;
}