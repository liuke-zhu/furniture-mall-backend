package com.mall.vo.admin.dashboard;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class DashboardOverviewResponse {
    private BigDecimal todaySales;
    private Long todayOrderCount;
    private Long totalUserCount;
    private Long totalProductCount;
    private Long pendingShipCount;
}
