package com.mall.controller.app;

import com.mall.common.annotation.Idempotent;
import com.mall.common.api.Result;
import com.mall.dto.order.OrderSubmitRequest;
import com.mall.dto.order.PayRequest;
import com.mall.service.app.OrderService;
import com.mall.vo.order.OrderResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Idempotent(interval = 5, message = "请勿重复提交订单")
    @PostMapping("/submit")
    public Result<OrderResponse> submit(@Valid @RequestBody OrderSubmitRequest request) {
        return Result.success("下单成功", orderService.submit(request));
    }

    @GetMapping("/list")
    public Result<List<OrderResponse>> list() {
        return Result.success(orderService.list());
    }

    @GetMapping("/{orderNo}")
    public Result<OrderResponse> detail(@PathVariable String orderNo) {
        return Result.success(orderService.detail(orderNo));
    }

    @PutMapping("/cancel/{orderNo}")
    public Result<Void> cancel(@PathVariable String orderNo) {
        orderService.cancel(orderNo);
        return Result.success("取消订单成功", null);
    }

    @PutMapping("/confirm/{orderNo}")
    public Result<Void> confirm(@PathVariable String orderNo) {
        orderService.confirm(orderNo);
        return Result.success("确认收货成功", null);
    }

    @Idempotent(interval = 3, message = "请勿重复支付")
    @PutMapping("/pay/{orderNo}")
    public Result<Void> pay(@PathVariable String orderNo, @Valid @RequestBody PayRequest request) {
        orderService.pay(orderNo, request);
        return Result.success("支付成功", null);
    }
}
