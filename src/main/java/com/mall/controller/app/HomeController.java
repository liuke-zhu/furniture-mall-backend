package com.mall.controller.app;

import com.mall.common.api.Result;
import com.mall.service.app.HomeService;
import com.mall.vo.home.HomeDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping
    public Result<HomeDataResponse> home() {
        return Result.success(homeService.getHomeData());
    }

    @GetMapping("/banners")
    public Result<HomeDataResponse> banners() {
        return Result.success(homeService.getHomeData());
    }
}
