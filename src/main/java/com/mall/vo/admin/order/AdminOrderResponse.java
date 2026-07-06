package com.mall.vo.admin.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class AdminOrderResponse {
    private String orderNo;
    private Long userId;
    private String username;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private BigDecimal freightAmount;
    private Integer orderStatus;
    private Integer payType;
    private LocalDateTime paymentTime;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<AdminOrderItemResponse> items;
}
