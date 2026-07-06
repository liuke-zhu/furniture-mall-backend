package com.mall.service.impl.app;

import com.mall.common.exception.BusinessException;
import com.mall.common.util.RequestContext;
import com.mall.dto.order.OrderSubmitRequest;
import com.mall.dto.order.PayRequest;
import com.mall.entity.Address;
import com.mall.entity.Cart;
import com.mall.entity.Order;
import com.mall.entity.OrderItem;
import com.mall.entity.Product;
import com.mall.mapper.AddressMapper;
import com.mall.mapper.CartMapper;
import com.mall.mapper.OrderItemMapper;
import com.mall.mapper.OrderMapper;
import com.mall.mapper.ProductMapper;
import com.mall.service.app.OrderService;
import com.mall.vo.order.OrderItemResponse;
import com.mall.vo.order.OrderResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final DateTimeFormatter ORDER_NO_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final CartMapper cartMapper;
    private final ProductMapper productMapper;
    private final AddressMapper addressMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Override
    @Transactional
    public OrderResponse submit(OrderSubmitRequest request) {
        Long userId = currentUserId();

        Address address = addressMapper.selectByIdAndUserId(request.getAddressId(), userId);
        if (address == null) {
            throw new BusinessException("收货地址不存在");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        List<Long> cartIds = new ArrayList<>();
        String orderNo = generateOrderNo();

        if (request.getItems() != null && !request.getItems().isEmpty()) {
            // 直接购买模式
            for (OrderSubmitRequest.DirectItem di : request.getItems()) {
                Product product = productMapper.selectById(di.getProductId());
                if (product == null) {
                    throw new BusinessException("商品已下架，无法购买");
                }
                if (product.getStock() < di.getQuantity()) {
                    throw new BusinessException("商品 " + product.getName() + " 库存不足");
                }
                if (productMapper.deductStock(product.getId(), di.getQuantity()) == 0) {
                    throw new BusinessException("商品 " + product.getName() + " 扣减库存失败");
                }
                BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(di.getQuantity()));
                totalAmount = totalAmount.add(itemTotal);

                OrderItem item = new OrderItem();
                item.setOrderNo(orderNo);
                item.setProductId(product.getId());
                item.setProductName(product.getName());
                item.setProductImage(product.getCoverImage());
                item.setProductPrice(product.getPrice());
                item.setQuantity(di.getQuantity());
                item.setTotalPrice(itemTotal);
                orderItems.add(item);
            }
        } else {
            // 购物车结算模式
            List<Cart> checkedCarts = cartMapper.selectCheckedList(userId);
            if (checkedCarts.isEmpty()) {
                throw new BusinessException("请选择要结算的商品");
            }
            for (Cart cart : checkedCarts) {
                Product product = productMapper.selectById(cart.getProductId());
                if (product == null) {
                    throw new BusinessException("存在已下架商品，无法提交订单");
                }
                if (product.getStock() < cart.getQuantity()) {
                    throw new BusinessException("商品 " + product.getName() + " 库存不足");
                }
                if (productMapper.deductStock(product.getId(), cart.getQuantity()) == 0) {
                    throw new BusinessException("商品 " + product.getName() + " 扣减库存失败");
                }
                BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
                totalAmount = totalAmount.add(itemTotal);
                cartIds.add(cart.getId());

                OrderItem item = new OrderItem();
                item.setOrderNo(orderNo);
                item.setProductId(product.getId());
                item.setProductName(product.getName());
                item.setProductImage(product.getCoverImage());
                item.setProductPrice(product.getPrice());
                item.setQuantity(cart.getQuantity());
                item.setTotalPrice(itemTotal);
                orderItems.add(item);
            }
        }

        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount);
        order.setFreightAmount(BigDecimal.ZERO);
        order.setOrderStatus(0);
        order.setPayType(1);
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setReceiverAddress(address.getProvince() + address.getCity() + address.getDistrict() + address.getDetailAddress());
        order.setRemark(request.getRemark());
        order.setCreateTime(LocalDateTime.now());
        orderMapper.insert(order);

        orderItems.forEach(item -> item.setOrderId(order.getId()));
        orderItemMapper.batchInsert(orderItems);

        // 购物车结算模式才清购物车
        if (!cartIds.isEmpty()) {
            cartMapper.deleteCheckedByIds(userId, cartIds);
        }

        return buildOrderResponse(order, orderItems);
    }

    @Override
    public List<OrderResponse> list() {
        Long userId = currentUserId();
        List<Order> orders = orderMapper.selectByUserId(userId);
        return orders.stream().map(order -> {
            List<OrderItem> items = orderItemMapper.selectByOrderNo(order.getOrderNo());
            return buildOrderResponse(order, items);
        }).toList();
    }

    @Override
    public OrderResponse detail(String orderNo) {
        Order order = requireOrder(orderNo);
        return buildOrderResponse(order, orderItemMapper.selectByOrderNo(orderNo));
    }

    @Override
    @Transactional
    public void cancel(String orderNo) {
        Order order = requireOrder(orderNo);
        if (order.getOrderStatus() != 0) {
            throw new BusinessException("当前订单状态不允许取消");
        }
        orderMapper.updateStatus(orderNo, currentUserId(), 4);
        List<OrderItem> items = orderItemMapper.selectByOrderNo(orderNo);
        for (OrderItem item : items) {
            productMapper.restoreStock(item.getProductId(), item.getQuantity());
        }
    }

    @Override
    public void confirm(String orderNo) {
        Order order = requireOrder(orderNo);
        if (order.getOrderStatus() != 2) {
            throw new BusinessException("当前订单不是待收货状态");
        }
        orderMapper.updateStatus(orderNo, currentUserId(), 3);
    }

    @Override
    @Transactional
    public void pay(String orderNo, PayRequest request) {
        Order order = requireOrder(orderNo);
        if (order.getOrderStatus() != 0) {
            throw new BusinessException("当前订单状态不允许支付");
        }
        int affected = orderMapper.updateToPaid(orderNo, currentUserId(), request.getPayType());
        if (affected == 0) {
            throw new BusinessException("支付失败，订单可能已被处理");
        }
    }

    private Order requireOrder(String orderNo) {
        Order order = orderMapper.selectByOrderNoAndUserId(orderNo, currentUserId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return order;
    }

    private OrderResponse buildOrderResponse(Order order, List<OrderItem> items) {
        OrderResponse response = new OrderResponse();
        response.setOrderNo(order.getOrderNo());
        response.setTotalAmount(order.getTotalAmount());
        response.setPayAmount(order.getPayAmount());
        response.setOrderStatus(order.getOrderStatus());
        response.setPayType(order.getPayType());
        response.setPaymentTime(order.getPaymentTime());
        response.setReceiverName(order.getReceiverName());
        response.setReceiverPhone(order.getReceiverPhone());
        response.setReceiverAddress(order.getReceiverAddress());
        response.setRemark(order.getRemark());
        response.setCreateTime(order.getCreateTime());
        response.setItems(items.stream().map(this::toOrderItemResponse).toList());
        return response;
    }

    private OrderItemResponse toOrderItemResponse(OrderItem item) {
        OrderItemResponse response = new OrderItemResponse();
        response.setProductId(item.getProductId());
        response.setProductName(item.getProductName());
        response.setProductImage(item.getProductImage());
        response.setProductPrice(item.getProductPrice());
        response.setQuantity(item.getQuantity());
        response.setTotalPrice(item.getTotalPrice());
        return response;
    }

    private Long currentUserId() {
        Long userId = RequestContext.getUserId();
        if (userId == null) {
            throw new BusinessException("未获取到当前登录用户");
        }
        return userId;
    }

    private String generateOrderNo() {
        return ORDER_NO_FORMATTER.format(LocalDateTime.now()) + ThreadLocalRandom.current().nextInt(100000, 999999);
    }
}
