package com.mall.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Category {
    private Long id;
    private Long parentId;
    private String name;
    private Integer sort;
    private String icon;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
