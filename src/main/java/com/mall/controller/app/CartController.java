package com.mall.controller.app;

import com.mall.common.api.Result;
import com.mall.dto.cart.AddCartRequest;
import com.mall.dto.cart.CheckCartRequest;
import com.mall.dto.cart.UpdateCartRequest;
import com.mall.service.app.CartService;
import com.mall.vo.cart.CartItemResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public Result<Void> add(@Valid @RequestBody AddCartRequest request) {
        cartService.add(request);
        return Result.success("加入购物车成功", null);
    }

    @GetMapping("/list")
    public Result<List<CartItemResponse>> list() {
        return Result.success(cartService.list());
    }

    @PutMapping("/update")
    public Result<Void> update(@Valid @RequestBody UpdateCartRequest request) {
        cartService.update(request);
        return Result.success("更新购物车成功", null);
    }

    @PutMapping("/check")
    public Result<Void> check(@Valid @RequestBody CheckCartRequest request) {
        cartService.updateChecked(request);
        return Result.success("更新勾选状态成功", null);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        cartService.delete(id);
        return Result.success("删除成功", null);
    }
}
