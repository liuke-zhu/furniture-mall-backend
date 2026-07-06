package com.mall.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PayRequest {

    @NotNull(message = "支付方式不能为空")
    private Integer payType; // 1=微信 2=支付宝（mock）
}
