package com.mall.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Favorite {
    private Long id;
    private Long userId;
    private Long productId;
    private LocalDateTime createTime;
}
