package com.mall.vo.order;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class OrderItemResponse {
    private Long productId;
    private String productName;
    private String productImage;
    private BigDecimal productPrice;
    private Integer quantity;
    private BigDecimal totalPrice;
}
