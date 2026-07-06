package com.mall.controller.admin;

import com.mall.common.api.Result;
import com.mall.service.admin.AdminDashboardService;
import com.mall.vo.admin.dashboard.DashboardOverviewResponse;
import com.mall.vo.admin.dashboard.OrderStatusStatResponse;
import com.mall.vo.admin.dashboard.SalesTrendResponse;
import com.mall.vo.admin.dashboard.UserGrowthResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    @GetMapping("/overview")
    public Result<DashboardOverviewResponse> overview() {
        return Result.success(dashboardService.overview());
    }

    @GetMapping("/sales-trend")
    public Result<List<SalesTrendResponse>> salesTrend() {
        return Result.success(dashboardService.salesTrend());
    }

    @GetMapping("/order-status")
    public Result<List<OrderStatusStatResponse>> orderStatusStat() {
        return Result.success(dashboardService.orderStatusStat());
    }

    @GetMapping("/user-growth")
    public Result<List<UserGrowthResponse>> userGrowth() {
        return Result.success(dashboardService.userGrowth());
    }
}
