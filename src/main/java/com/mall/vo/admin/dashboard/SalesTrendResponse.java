package com.mall.vo.admin.dashboard;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class SalesTrendResponse {
    private String date;
    private BigDecimal sales;
    private Long orderCount;
}
