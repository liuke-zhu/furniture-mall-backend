package com.mall.service.impl.admin;

import com.mall.mapper.DashboardMapper;
import com.mall.service.admin.AdminDashboardService;
import com.mall.vo.admin.dashboard.DashboardOverviewResponse;
import com.mall.vo.admin.dashboard.OrderStatusStatResponse;
import com.mall.vo.admin.dashboard.SalesTrendResponse;
import com.mall.vo.admin.dashboard.UserGrowthResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE;

    private final DashboardMapper dashboardMapper;

    @Override
    public DashboardOverviewResponse overview() {
        DashboardOverviewResponse response = new DashboardOverviewResponse();
        response.setTodaySales(dashboardMapper.sumTodaySales());
        response.setTodayOrderCount(dashboardMapper.countTodayOrders());
        response.setTotalUserCount(dashboardMapper.countTotalUsers());
        response.setTotalProductCount(dashboardMapper.countOnSaleProducts());
        response.setPendingShipCount(dashboardMapper.countPendingShip());
        return response;
    }

    @Override
    public List<SalesTrendResponse> salesTrend() {
        List<SalesTrendResponse> db = dashboardMapper.selectSalesTrend7d();
        Map<String, SalesTrendResponse> map = db.stream()
                .collect(Collectors.toMap(SalesTrendResponse::getDate, Function.identity()));
        List<SalesTrendResponse> result = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            String date = LocalDate.now().minusDays(i).format(DATE_FORMATTER);
            SalesTrendResponse item = map.get(date);
            if (item == null) {
                item = new SalesTrendResponse();
                item.setDate(date);
                item.setSales(BigDecimal.ZERO);
                item.setOrderCount(0L);
            }
            result.add(item);
        }
        return result;
    }

    @Override
    public List<OrderStatusStatResponse> orderStatusStat() {
        return dashboardMapper.selectOrderStatusStat();
    }

    @Override
    public List<UserGrowthResponse> userGrowth() {
        List<UserGrowthResponse> db = dashboardMapper.selectUserGrowth7d();
        Map<String, UserGrowthResponse> map = db.stream()
                .collect(Collectors.toMap(UserGrowthResponse::getDate, Function.identity()));
        List<UserGrowthResponse> result = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            String date = LocalDate.now().minusDays(i).format(DATE_FORMATTER);
            UserGrowthResponse item = map.get(date);
            if (item == null) {
                item = new UserGrowthResponse();
                item.setDate(date);
                item.setNewUsers(0L);
            }
            result.add(item);
        }
        return result;
    }
}
