package com.mall.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ProductDetail {
    private Long id;
    private Long productId;
    private String detailImages;
    private String material;
    private String sizeInfo;
    private String colorInfo;
    private String styleInfo;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
