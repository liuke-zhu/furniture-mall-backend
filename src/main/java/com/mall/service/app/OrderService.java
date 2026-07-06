package com.mall.service.app;

import com.mall.dto.order.OrderSubmitRequest;
import com.mall.dto.order.PayRequest;
import com.mall.vo.order.OrderResponse;
import java.util.List;

public interface OrderService {

    OrderResponse submit(OrderSubmitRequest request);

    List<OrderResponse> list();

    OrderResponse detail(String orderNo);

    void cancel(String orderNo);

    void confirm(String orderNo);

    void pay(String orderNo, PayRequest request);
}
