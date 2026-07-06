package com.mall.dto.admin.product;

import lombok.Data;

@Data
public class ProductDetailRequest {
    private String detailImages;
    private String material;
    private String sizeInfo;
    private String colorInfo;
    private String styleInfo;
    private String description;
}
