package com.mall.controller.admin;

import com.mall.common.annotation.OperationLog;
import com.mall.common.api.PageResult;
import com.mall.common.api.Result;
import com.mall.service.admin.AdminOrderService;
import com.mall.vo.admin.order.AdminOrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/order")
@RequiredArgsConstructor
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    @GetMapping("/page")
    public Result<PageResult<AdminOrderResponse>> page(@RequestParam(required = false) String orderNo,
                                                       @RequestParam(required = false) Integer status,
                                                       @RequestParam(required = false) Integer pageNum,
                                                       @RequestParam(required = false) Integer pageSize) {
        return Result.success(adminOrderService.page(orderNo, status, pageNum, pageSize));
    }

    @GetMapping("/{orderNo}")
    public Result<AdminOrderResponse> detail(@PathVariable String orderNo) {
        return Result.success(adminOrderService.detail(orderNo));
    }

    @PutMapping("/ship/{orderNo}")
    @OperationLog(module = "订单管理", type = "发货", desc = "订单发货")
    public Result<Void> ship(@PathVariable String orderNo) {
        adminOrderService.ship(orderNo);
        return Result.success("发货成功", null);
    }

    @PutMapping("/close/{orderNo}")
    @OperationLog(module = "订单管理", type = "关闭", desc = "关闭订单")
    public Result<Void> close(@PathVariable String orderNo) {
        adminOrderService.close(orderNo);
        return Result.success("关闭订单成功", null);
    }
}
