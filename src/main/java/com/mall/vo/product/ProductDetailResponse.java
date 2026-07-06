package com.mall.vo.product;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class ProductDetailResponse {
    private Long id;
    private Long categoryId;
    private String name;
    private String subTitle;
    private String coverImage;
    private BigDecimal price;
    private Integer stock;
    private Integer sales;
    private String detailImages;
    private String material;
    private String sizeInfo;
    private String colorInfo;
    private String styleInfo;
    private String description;
}
