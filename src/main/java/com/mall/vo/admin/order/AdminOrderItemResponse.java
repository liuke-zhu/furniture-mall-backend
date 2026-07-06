package com.mall.vo.admin.order;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class AdminOrderItemResponse {
    private Long productId;
    private String productName;
    private String productImage;
    private BigDecimal productPrice;
    private Integer quantity;
    private BigDecimal totalPrice;
}
