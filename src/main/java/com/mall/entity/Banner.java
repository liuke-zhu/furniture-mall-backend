package com.mall.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Banner {
    private Long id;
    private String title;
    private String imageUrl;
    private String linkUrl;
    private Integer sort;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
