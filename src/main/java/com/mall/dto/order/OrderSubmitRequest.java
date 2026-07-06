package com.mall.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
public class OrderSubmitRequest {

    @NotNull(message = "收货地址 ID 不能为空")
    private Long addressId;

    private String remark;

    /** 直接购买模式时传（购物车结算不传） */
    @Valid
    private List<DirectItem> items;

    @Data
    public static class DirectItem {
        @NotNull(message = "商品 ID 不能为空")
        private Long productId;
        @NotNull(message = "购买数量不能为空")
        @Min(value = 1, message = "购买数量至少为 1")
        private Integer quantity;
    }
}
