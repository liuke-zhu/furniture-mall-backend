package com.mall.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Product {
    private Long id;
    private Long categoryId;
    private String name;
    private String subTitle;
    private String coverImage;
    private BigDecimal price;
    private Integer stock;
    private Integer sales;
    private Integer status;
    private Integer isHot;
    private Integer isNew;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
