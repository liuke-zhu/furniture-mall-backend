package com.mall.service.admin;

import com.mall.vo.admin.dashboard.DashboardOverviewResponse;
import com.mall.vo.admin.dashboard.OrderStatusStatResponse;
import com.mall.vo.admin.dashboard.SalesTrendResponse;
import com.mall.vo.admin.dashboard.UserGrowthResponse;
import java.util.List;

public interface AdminDashboardService {

    DashboardOverviewResponse overview();

    List<SalesTrendResponse> salesTrend();

    List<OrderStatusStatResponse> orderStatusStat();

    List<UserGrowthResponse> userGrowth();
}
