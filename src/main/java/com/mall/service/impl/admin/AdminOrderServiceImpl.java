package com.mall.service.impl.admin;

import com.mall.common.api.PageResult;
import com.mall.common.exception.BusinessException;
import com.mall.entity.Order;
import com.mall.entity.OrderItem;
import com.mall.mapper.OrderItemMapper;
import com.mall.mapper.OrderMapper;
import com.mall.mapper.ProductMapper;
import com.mall.service.admin.AdminOrderService;
import com.mall.vo.admin.order.AdminOrderItemResponse;
import com.mall.vo.admin.order.AdminOrderResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminOrderServiceImpl implements AdminOrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;

    @Override
    public PageResult<AdminOrderResponse> page(String orderNo, Integer status, Integer pageNum, Integer pageSize) {
        int validPageNum = normalizePageNum(pageNum);
        int validPageSize = normalizePageSize(pageSize);
        long total = orderMapper.countAdmin(orderNo, status);
        List<Order> orders = orderMapper.selectAdminPage(orderNo, status, (validPageNum - 1) * validPageSize, validPageSize);
        List<AdminOrderResponse> list = orders.stream()
                .map(order -> toResponse(order, orderItemMapper.selectByOrderNo(order.getOrderNo())))
                .toList();
        return new PageResult<>(total, validPageNum, validPageSize, list);
    }

    @Override
    public AdminOrderResponse detail(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return toResponse(order, orderItemMapper.selectByOrderNo(orderNo));
    }

    @Override
    public void ship(String orderNo) {
        Order order = requireOrder(orderNo);
        if (order.getOrderStatus() != 1) {
            throw new BusinessException("当前订单状态不允许发货");
        }
        orderMapper.updateStatusByOrderNo(orderNo, 2);
    }

    @Override
    @Transactional
    public void close(String orderNo) {
        Order order = requireOrder(orderNo);
        if (order.getOrderStatus() == 4) {
            throw new BusinessException("订单已关闭");
        }
        if (order.getOrderStatus() == 3) {
            throw new BusinessException("已完成订单不可关闭");
        }
        orderMapper.updateStatusByOrderNo(orderNo, 4);
        List<OrderItem> items = orderItemMapper.selectByOrderNo(orderNo);
        for (OrderItem item : items) {
            productMapper.restoreStock(item.getProductId(), item.getQuantity());
        }
    }

    private Order requireOrder(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return order;
    }

    private AdminOrderResponse toResponse(Order order, List<OrderItem> items) {
        AdminOrderResponse response = new AdminOrderResponse();
        response.setOrderNo(order.getOrderNo());
        response.setUserId(order.getUserId());
        response.setUsername(order.getUsername());
        response.setTotalAmount(order.getTotalAmount());
        response.setPayAmount(order.getPayAmount());
        response.setFreightAmount(order.getFreightAmount());
        response.setOrderStatus(order.getOrderStatus());
        response.setPayType(order.getPayType());
        response.setPaymentTime(order.getPaymentTime());
        response.setReceiverName(order.getReceiverName());
        response.setReceiverPhone(order.getReceiverPhone());
        response.setReceiverAddress(order.getReceiverAddress());
        response.setRemark(order.getRemark());
        response.setCreateTime(order.getCreateTime());
        response.setUpdateTime(order.getUpdateTime());
        response.setItems(items.stream().map(this::toItemResponse).toList());
        return response;
    }

    private AdminOrderItemResponse toItemResponse(OrderItem item) {
        AdminOrderItemResponse response = new AdminOrderItemResponse();
        response.setProductId(item.getProductId());
        response.setProductName(item.getProductName());
        response.setProductImage(item.getProductImage());
        response.setProductPrice(item.getProductPrice());
        response.setQuantity(item.getQuantity());
        response.setTotalPrice(item.getTotalPrice());
        return response;
    }

    private int normalizePageNum(Integer pageNum) {
        return pageNum == null || pageNum < 1 ? 1 : pageNum;
    }

    private int normalizePageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1) {
            return 10;
        }
        return Math.min(pageSize, 50);
    }
}
