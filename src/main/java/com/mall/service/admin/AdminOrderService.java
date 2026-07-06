package com.mall.service.admin;

import com.mall.common.api.PageResult;
import com.mall.vo.admin.order.AdminOrderResponse;

public interface AdminOrderService {

    PageResult<AdminOrderResponse> page(String orderNo, Integer status, Integer pageNum, Integer pageSize);

    AdminOrderResponse detail(String orderNo);

    void ship(String orderNo);

    void close(String orderNo);
}
