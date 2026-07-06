package com.mall.service.impl.app;

import com.mall.common.exception.BusinessException;
import com.mall.common.util.RequestContext;
import com.mall.dto.cart.AddCartRequest;
import com.mall.dto.cart.CheckCartRequest;
import com.mall.dto.cart.UpdateCartRequest;
import com.mall.entity.Cart;
import com.mall.entity.Product;
import com.mall.mapper.CartMapper;
import com.mall.mapper.ProductMapper;
import com.mall.service.app.CartService;
import com.mall.vo.cart.CartItemResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper;
    private final ProductMapper productMapper;

    @Override
    public void add(AddCartRequest request) {
        Long userId = currentUserId();
        Product product = requireProduct(request.getProductId());
        if (product.getStock() < request.getQuantity()) {
            throw new BusinessException("商品库存不足");
        }
        Cart cart = cartMapper.selectByUserIdAndProductId(userId, request.getProductId());
        if (cart == null) {
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            newCart.setProductId(request.getProductId());
            newCart.setQuantity(request.getQuantity());
            newCart.setChecked(1);
            cartMapper.insert(newCart);
            return;
        }
        int targetQuantity = cart.getQuantity() + request.getQuantity();
        if (product.getStock() < targetQuantity) {
            throw new BusinessException("加入购物车失败，库存不足");
        }
        cart.setUserId(userId);
        cart.setQuantity(targetQuantity);
        cart.setChecked(1);
        cartMapper.updateByIdAndUserId(cart);
    }

    @Override
    public void update(UpdateCartRequest request) {
        Long userId = currentUserId();
        List<CartItemResponse> cartList = cartMapper.selectCartList(userId);
        CartItemResponse target = cartList.stream().filter(item -> item.getCartId().equals(request.getCartId())).findFirst()
                .orElseThrow(() -> new BusinessException("购物车记录不存在"));
        if (target.getStock() < request.getQuantity()) {
            throw new BusinessException("库存不足");
        }
        Cart cart = new Cart();
        cart.setId(request.getCartId());
        cart.setUserId(userId);
        cart.setProductId(target.getProductId());
        cart.setQuantity(request.getQuantity());
        cart.setChecked(target.getChecked());
        cartMapper.updateByIdAndUserId(cart);
    }

    @Override
    public void updateChecked(CheckCartRequest request) {
        Long userId = currentUserId();
        cartMapper.updateChecked(userId, request.getCartIds(), request.getChecked());
    }

    @Override
    public void delete(Long cartId) {
        Long userId = currentUserId();
        if (cartMapper.deleteByIdAndUserId(cartId, userId) == 0) {
            throw new BusinessException("购物车记录不存在");
        }
    }

    @Override
    public List<CartItemResponse> list() {
        return cartMapper.selectCartList(currentUserId());
    }

    private Long currentUserId() {
        Long userId = RequestContext.getUserId();
        if (userId == null) {
            throw new BusinessException("未获取到当前登录用户");
        }
        return userId;
    }

    private Product requireProduct(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在或已下架");
        }
        return product;
    }
}
