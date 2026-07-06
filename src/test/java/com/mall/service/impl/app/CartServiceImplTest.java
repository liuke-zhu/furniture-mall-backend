package com.mall.service.impl.app;

import com.mall.common.exception.BusinessException;
import com.mall.common.util.RequestContext;
import com.mall.dto.cart.AddCartRequest;
import com.mall.dto.cart.UpdateCartRequest;
import com.mall.entity.Cart;
import com.mall.entity.Product;
import com.mall.mapper.CartMapper;
import com.mall.mapper.ProductMapper;
import com.mall.vo.cart.CartItemResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    private static final Long USER_ID = 1L;

    @Mock
    private CartMapper cartMapper;
    @Mock
    private ProductMapper productMapper;
    @InjectMocks
    private CartServiceImpl cartService;

    @BeforeEach
    void setUp() {
        RequestContext.set(USER_ID, "testuser", "USER");
    }

    @AfterEach
    void tearDown() {
        RequestContext.clear();
    }

    @Test
    void add_shouldInsert_whenCartNotExists() {
        AddCartRequest request = new AddCartRequest();
        request.setProductId(100L);
        request.setQuantity(1);

        Product product = new Product();
        product.setId(100L);
        product.setStock(10);
        when(productMapper.selectById(100L)).thenReturn(product);
        when(cartMapper.selectByUserIdAndProductId(USER_ID, 100L)).thenReturn(null);

        cartService.add(request);

        verify(cartMapper).insert(argThat(c ->
                c.getUserId().equals(USER_ID)
                        && c.getProductId().equals(100L)
                        && c.getQuantity() == 1
                        && c.getChecked() == 1));
    }

    @Test
    void add_shouldUpdateQuantity_whenCartExists() {
        AddCartRequest request = new AddCartRequest();
        request.setProductId(100L);
        request.setQuantity(2);

        Product product = new Product();
        product.setId(100L);
        product.setStock(10);
        Cart existing = new Cart();
        existing.setId(5L);
        existing.setQuantity(3);

        when(productMapper.selectById(100L)).thenReturn(product);
        when(cartMapper.selectByUserIdAndProductId(USER_ID, 100L)).thenReturn(existing);

        cartService.add(request);

        verify(cartMapper).updateByIdAndUserId(argThat(c ->
                c.getId().equals(5L) && c.getQuantity() == 5));
    }

    @Test
    void add_shouldThrow_whenStockInsufficient() {
        AddCartRequest request = new AddCartRequest();
        request.setProductId(100L);
        request.setQuantity(5);

        Product product = new Product();
        product.setId(100L);
        product.setStock(2);
        when(productMapper.selectById(100L)).thenReturn(product);

        assertThatThrownBy(() -> cartService.add(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("库存不足");
        verify(cartMapper, never()).insert(any());
    }

    @Test
    void update_shouldThrow_whenCartItemNotFound() {
        UpdateCartRequest request = new UpdateCartRequest();
        request.setCartId(99L);
        request.setQuantity(1);
        when(cartMapper.selectCartList(USER_ID)).thenReturn(List.of());

        assertThatThrownBy(() -> cartService.update(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("购物车记录不存在");
    }

    @Test
    void update_shouldThrow_whenStockInsufficient() {
        UpdateCartRequest request = new UpdateCartRequest();
        request.setCartId(1L);
        request.setQuantity(10);

        CartItemResponse item = new CartItemResponse();
        item.setCartId(1L);
        item.setProductId(100L);
        item.setStock(3);
        item.setChecked(1);
        when(cartMapper.selectCartList(USER_ID)).thenReturn(List.of(item));

        assertThatThrownBy(() -> cartService.update(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("库存不足");
    }

    @Test
    void delete_shouldThrow_whenCartNotFound() {
        when(cartMapper.deleteByIdAndUserId(99L, USER_ID)).thenReturn(0);

        assertThatThrownBy(() -> cartService.delete(99L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("购物车记录不存在");
    }
}
