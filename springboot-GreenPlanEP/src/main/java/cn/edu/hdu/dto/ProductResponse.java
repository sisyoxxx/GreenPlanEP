package cn.edu.hdu.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProductResponse {
    private List<ProductDTO> list; // 当前页的商品列表
    private long total;         // 总记录数
    private int page;           // 当前页码
    private int pageSize;       // 每页条数

    public ProductResponse(List<ProductDTO> list, long total, int page, int pageSize) {
        this.list = list;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
    }
}