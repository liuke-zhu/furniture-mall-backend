package com.mall.service.app;

import com.mall.dto.cart.AddCartRequest;
import com.mall.dto.cart.CheckCartRequest;
import com.mall.dto.cart.UpdateCartRequest;
import com.mall.vo.cart.CartItemResponse;
import java.util.List;

public interface CartService {

    void add(AddCartRequest request);

    void update(UpdateCartRequest request);

    void updateChecked(CheckCartRequest request);

    void delete(Long cartId);

    List<CartItemResponse> list();
}
