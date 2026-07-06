package com.mall.mapper;

import com.mall.vo.admin.dashboard.OrderStatusStatResponse;
import com.mall.vo.admin.dashboard.SalesTrendResponse;
import com.mall.vo.admin.dashboard.UserGrowthResponse;
import java.math.BigDecimal;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DashboardMapper {

    @Select("select coalesce(sum(pay_amount), 0) from oms_order where order_status >= 1 and date(create_time) = curdate()")
    BigDecimal sumTodaySales();

    @Select("select count(1) from oms_order where date(create_time) = curdate()")
    Long countTodayOrders();

    @Select("select count(1) from ums_user")
    Long countTotalUsers();

    @Select("select count(1) from pms_product where status = 1")
    Long countOnSaleProducts();

    @Select("select count(1) from oms_order where order_status = 1")
    Long countPendingShip();

    @Select("""
            select date_format(create_time, '%Y-%m-%d') as date,
                   coalesce(sum(case when order_status >= 1 then pay_amount else 0 end), 0) as sales,
                   count(1) as order_count
            from oms_order
            where create_time >= date_sub(curdate(), interval 6 day)
            group by date_format(create_time, '%Y-%m-%d')
            order by date asc
            """)
    List<SalesTrendResponse> selectSalesTrend7d();

    @Select("select order_status, count(1) as count from oms_order group by order_status")
    List<OrderStatusStatResponse> selectOrderStatusStat();

    @Select("""
            select date_format(create_time, '%Y-%m-%d') as date, count(1) as new_users
            from ums_user
            where create_time >= date_sub(curdate(), interval 6 day)
            group by date_format(create_time, '%Y-%m-%d')
            order by date asc
            """)
    List<UserGrowthResponse> selectUserGrowth7d();
}
