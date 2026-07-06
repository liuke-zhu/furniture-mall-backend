package com.mall.controller.app;

import com.mall.common.api.PageResult;
import com.mall.common.api.Result;
import com.mall.entity.Product;
import com.mall.service.app.ProductService;
import com.mall.vo.product.ProductDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/page")
    public Result<PageResult<Product>> page(@RequestParam(required = false) Integer pageNum,
                                            @RequestParam(required = false) Integer pageSize) {
        return Result.success(productService.page(pageNum, pageSize));
    }

    @GetMapping("/search")
    public Result<PageResult<Product>> search(@RequestParam(required = false) Long categoryId,
                                              @RequestParam(required = false) String keyword,
                                              @RequestParam(required = false) Integer pageNum,
                                              @RequestParam(required = false) Integer pageSize) {
        return Result.success(productService.search(categoryId, keyword, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<ProductDetailResponse> detail(@PathVariable Long id) {
        return Result.success(productService.detail(id));
    }
}
