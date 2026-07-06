package com.mall.controller.app;

import com.mall.common.api.Result;
import com.mall.service.app.FavoriteService;
import com.mall.vo.favorite.FavoriteResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/favorite")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/add/{productId}")
    public Result<Void> add(@PathVariable("productId") Long productId) {
        favoriteService.add(productId);
        return Result.success("收藏成功", null);
    }

    @DeleteMapping("/delete/{productId}")
    public Result<Void> delete(@PathVariable("productId") Long productId) {
        favoriteService.delete(productId);
        return Result.success("取消收藏成功", null);
    }

    @GetMapping("/list")
    public Result<List<FavoriteResponse>> list() {
        return Result.success(favoriteService.list());
    }

    @GetMapping("/check/{productId}")
    public Result<Boolean> check(@PathVariable("productId") Long productId) {
        return Result.success(favoriteService.check(productId));
    }
}
