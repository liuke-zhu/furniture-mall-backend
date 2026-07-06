package com.mall.dto.cart;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
public class CheckCartRequest {

    @NotEmpty(message = "购物车 ID 列表不能为空")
    private List<Long> cartIds;

    @NotNull(message = "勾选状态不能为空")
    private Integer checked;
}
