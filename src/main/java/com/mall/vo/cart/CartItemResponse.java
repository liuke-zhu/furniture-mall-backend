package com.mall.vo.cart;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class CartItemResponse {
    private Long cartId;
    private Long productId;
    private String productName;
    private String coverImage;
    private BigDecimal price;
    private Integer stock;
    private Integer quantity;
    private Integer checked;
    private BigDecimal totalPrice;
}
