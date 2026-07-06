package com.mall.vo.favorite;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class FavoriteResponse {
    private Long productId;
    private String productName;
    private String coverImage;
    private BigDecimal price;
    private Integer status;
    private LocalDateTime favoriteTime;
}
