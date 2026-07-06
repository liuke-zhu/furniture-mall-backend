package com.mall.dto.admin.product;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductStatusRequest {

    @NotNull(message = "状态不能为空")
    private Integer status;
}
