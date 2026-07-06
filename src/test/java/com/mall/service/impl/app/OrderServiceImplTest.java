package com.mall.service.impl.app;

import com.mall.common.exception.BusinessException;
import com.mall.common.util.RequestContext;
import com.mall.dto.order.PayRequest;
import com.mall.entity.Order;
import com.mall.entity.OrderItem;
import com.mall.mapper.AddressMapper;
import com.mall.mapper.CartMapper;
import com.mall.mapper.OrderItemMapper;
import com.mall.mapper.OrderMapper;
import com.mall.mapper.ProductMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    private static final Long USER_ID = 1L;
    private static final String ORDER_NO = "20260101120000123456";

    @Mock
    private CartMapper cartMapper;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private AddressMapper addressMapper;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private OrderItemMapper orderItemMapper;
    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        RequestContext.set(USER_ID, "testuser", "USER");
    }

    @AfterEach
    void tearDown() {
        RequestContext.clear();
    }

    @Test
    void pay_shouldSucceed_whenOrderPendingPayment() {
        Order order = pendingOrder();
        when(orderMapper.selectByOrderNoAndUserId(ORDER_NO, USER_ID)).thenReturn(order);
        when(orderMapper.updateToPaid(ORDER_NO, USER_ID, 1)).thenReturn(1);

        PayRequest request = new PayRequest();
        request.setPayType(1);
        orderService.pay(ORDER_NO, request);

        verify(orderMapper).updateToPaid(ORDER_NO, USER_ID, 1);
    }

    @Test
    void pay_shouldThrow_whenOrderNotPendingPayment() {
        Order order = pendingOrder();
        order.setOrderStatus(1);
        when(orderMapper.selectByOrderNoAndUserId(ORDER_NO, USER_ID)).thenReturn(order);

        PayRequest request = new PayRequest();
        request.setPayType(1);

        assertThatThrownBy(() -> orderService.pay(ORDER_NO, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("不允许支付");
        verify(orderMapper, never()).updateToPaid(anyString(), anyLong(), anyInt());
    }

    @Test
    void pay_shouldThrow_whenUpdateAffectedZero() {
        Order order = pendingOrder();
        when(orderMapper.selectByOrderNoAndUserId(ORDER_NO, USER_ID)).thenReturn(order);
        when(orderMapper.updateToPaid(ORDER_NO, USER_ID, 2)).thenReturn(0);

        PayRequest request = new PayRequest();
        request.setPayType(2);

        assertThatThrownBy(() -> orderService.pay(ORDER_NO, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("支付失败");
    }

    @Test
    void cancel_shouldRestoreStock_whenOrderPendingPayment() {
        Order order = pendingOrder();
        when(orderMapper.selectByOrderNoAndUserId(ORDER_NO, USER_ID)).thenReturn(order);

        OrderItem item = new OrderItem();
        item.setProductId(10L);
        item.setQuantity(2);
        when(orderItemMapper.selectByOrderNo(ORDER_NO)).thenReturn(List.of(item));

        orderService.cancel(ORDER_NO);

        verify(orderMapper).updateStatus(ORDER_NO, USER_ID, 4);
        verify(productMapper).restoreStock(10L, 2);
    }

    @Test
    void cancel_shouldThrow_whenOrderAlreadyPaid() {
        Order order = pendingOrder();
        order.setOrderStatus(1);
        when(orderMapper.selectByOrderNoAndUserId(ORDER_NO, USER_ID)).thenReturn(order);

        assertThatThrownBy(() -> orderService.cancel(ORDER_NO))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("不允许取消");
        verify(orderMapper, never()).updateStatus(anyString(), anyLong(), anyInt());
        verify(productMapper, never()).restoreStock(anyLong(), anyInt());
    }

    @Test
    void confirm_shouldSucceed_whenOrderAwaitingReceipt() {
        Order order = pendingOrder();
        order.setOrderStatus(2);
        when(orderMapper.selectByOrderNoAndUserId(ORDER_NO, USER_ID)).thenReturn(order);

        orderService.confirm(ORDER_NO);

        verify(orderMapper).updateStatus(ORDER_NO, USER_ID, 3);
    }

    @Test
    void detail_shouldThrow_whenOrderNotFound() {
        when(orderMapper.selectByOrderNoAndUserId(ORDER_NO, USER_ID)).thenReturn(null);

        assertThatThrownBy(() -> orderService.detail(ORDER_NO))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("订单不存在");
    }

    private Order pendingOrder() {
        Order order = new Order();
        order.setOrderNo(ORDER_NO);
        order.setUserId(USER_ID);
        order.setOrderStatus(0);
        return order;
    }
}
