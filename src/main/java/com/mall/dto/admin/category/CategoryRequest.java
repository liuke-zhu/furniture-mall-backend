package com.mall.dto.admin.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryRequest {

    private Long id;

    @NotNull(message = "父分类 ID 不能为空")
    private Long parentId;

    @NotBlank(message = "分类名称不能为空")
    private String name;

    @NotNull(message = "排序不能为空")
    private Integer sort;

    private String icon;

    @NotNull(message = "状态不能为空")
    private Integer status;
}
